package ru.csc.bdse.controller;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.kv.KeyValueApi;
import ru.csc.bdse.kv.NodeAction;
import ru.csc.bdse.kv.NodeInfo;
import ru.csc.bdse.util.IllegalNodeStateException;

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

    private int numberOfOK;

    @Value("${node.WCL}")
    private int WCL;

    @Autowired
    public KeyValueApiController(final KeyValueApi keyValueApi,
                                 @Value("${bdse.nodes}") String nodesString,
                                 @Value("${server.port}") int port) {
        this.keyValueApi = keyValueApi;
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(8);
        this.numberOfOK = 0;

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
            throw new IllegalStateException("Error while recording");
        }

        return "COMMIT";
    }

    interface NodeClient {
        @RequestLine("PUT /key-value-inner/{key}")
        String putInner(@Param("key") final String key, final byte[] value);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/key-value/{key}")
    public byte[] get(@PathVariable final String key) {
        return keyValueApi.get(key)
                .orElseThrow(() -> new NoSuchElementException(key));
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
