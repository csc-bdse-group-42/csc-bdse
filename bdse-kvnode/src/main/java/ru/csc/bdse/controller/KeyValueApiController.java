package ru.csc.bdse.controller;

import feign.*;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.gson.GsonDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.kv.KeyValueApi;
import ru.csc.bdse.kv.NodeAction;
import ru.csc.bdse.kv.NodeInfo;
import ru.csc.bdse.model.KeyValueRecord;
import ru.csc.bdse.resolver.Resolver;
import ru.csc.bdse.util.IllegalNodeStateException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

/**
 * Provides HTTP API for the storage unit
 *
 * @author semkagtn
 */
@RestController
public class KeyValueApiController {
    private final KeyValueApi keyValueApi;

    private String[] nodeUrls;

    private int port;

    @Value("${node.timeout}")
    private long timeout;

    private ExecutorService threadPool;

    @Value("${node.WCL}")
    private int WCL;

    @Value("${node.RCL}")
    private int RCL;

    @Autowired
    public KeyValueApiController(final KeyValueApi keyValueApi,
                                 @Value("${bdse.nodes}") String nodesString,
                                 @Value("${server.port}") int port) {
        this.keyValueApi = keyValueApi;
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(8);

        if (nodesString != null) {
            nodeUrls = nodesString.split("(\\s|,)+");
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/key-value-inner/{key}")
    public String putInner(@PathVariable final String key,
                           @RequestBody final byte[] value) {
        String nodeStatus = keyValueApi.put(key, value);

        return nodeStatus;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/key-value/{key}")
    public String putOuter(@PathVariable final String key,
                           @RequestBody final byte[] value) throws InterruptedException, ExecutionException {

        int numberOfOK = 0;

        List<Future<String>> futures = new ArrayList<>();

        for (String nodeUrl : nodeUrls) {
            futures.add(
                    CompletableFuture.supplyAsync(
                            () -> {
                                NodeClient nodeClient = Feign.builder().target(NodeClient.class, "http://" + nodeUrl);
                                String nodeStatus = nodeClient.putInner(key, value);

                                return nodeStatus;
                            },
                            threadPool
                    ));
        }

        for (Future<String> future : futures) {
            String nodeStatus;
            try {
                nodeStatus = future.get(2, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                nodeStatus = "ABORT";
            }

            if (nodeStatus.equals("COMMIT")) {
                numberOfOK += 1;
            }
        }

        if (numberOfOK < WCL) {
            throw new IllegalStateException("Time error while recording");
        }

        return "COMMIT";
    }

    interface NodeClient {
        @RequestLine("PUT /key-value-inner/{key}")
        String putInner(@Param("key") final String key, final byte[] value);

        @RequestLine("GET /key-value-inner/{key}")
        @Headers("Content-Type: application/json")
        KeyValueRecord getInner(@Param("key") final String key);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/key-value-inner/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody KeyValueRecord getInner(@PathVariable final String key) {
        KeyValueRecord keyValueRecord = keyValueApi.get(key)
                .orElseThrow(() -> new NoSuchElementException(key));

        return keyValueRecord;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/key-value/{key}")
    public byte[] getOuter(@PathVariable final String key) throws ExecutionException, InterruptedException {

        int numberOfOK = 0;

        List<Future<KeyValueRecord>> futures = new ArrayList<>();

        for (String nodeUrl : nodeUrls) {
            futures.add(
                    CompletableFuture.supplyAsync(
                            () -> {
                                // todo: Feign не может распрарсить JSON объект KeyValueRecord, нужно его верно настроить или иначе распарсить
                                NodeClient nodeClient = Feign.builder().decoder(new GsonDecoder()).target(NodeClient.class, "http://" + nodeUrl);
                                KeyValueRecord optionalData = nodeClient.getInner(key);

                                return optionalData;
                            },
                            threadPool
                    ));
        }

        Set<KeyValueRecord> records = new HashSet<>();

        for (Future<KeyValueRecord> future : futures) {
            KeyValueRecord optionalData;
            try {
                optionalData = future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                optionalData = null;
            }

            if (optionalData != null) {
                System.out.println("Read OK");
                numberOfOK += 1;
                records.add(optionalData);
            }
        }

        if (numberOfOK < RCL) {
            throw new IllegalStateException("Time error while reading");
        }

        Resolver resolver = new Resolver();
        Optional<KeyValueRecord> record = resolver.resolve(records);

        if (record.isPresent()) {
            return record.get().getData();
        }

        throw new IllegalStateException();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/key-value")
    public Set<String> getKeys(@RequestParam("prefix") String prefix) {
        return keyValueApi.getKeys(prefix);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/key-value/{key}")
    public void delete(@PathVariable final String key) {
        keyValueApi.delete(key);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/info")
    public Set<NodeInfo> getInfo() {
        return keyValueApi.getInfo();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/action/{node}/{action}")
    public void action(@PathVariable final String node,
                       @PathVariable final NodeAction action) {
        keyValueApi.action(node, action);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle(NoSuchElementException e) {
        return Optional.ofNullable(e.getMessage()).orElse("");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(IllegalArgumentException e) {
        return Optional.ofNullable(e.getMessage()).orElse("");
    }

    @ExceptionHandler(IllegalNodeStateException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "IllegalNodeState")
    public String handle(IllegalNodeStateException e) {
        return Optional.ofNullable(e.getMessage()).orElse("IllegalNodeState");
    }
}
