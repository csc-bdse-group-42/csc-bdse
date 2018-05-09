package ru.csc.bdse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.kv.NodeInfo;
import ru.csc.bdse.kv.PartitionedKeyValueApi;
import ru.csc.bdse.model.KeyValueRecord;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("partitioned")
public class PartitionedKeyValueApiController {
    private PartitionedKeyValueApi partitionedKeyValueApi;

    @Autowired
    public PartitionedKeyValueApiController(PartitionedKeyValueApi partitionedKeyValueApi) {
        this.partitionedKeyValueApi = partitionedKeyValueApi;
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/kv/{key}")
    public String putOuter(@PathVariable final String key,
                           @RequestBody final byte[] value) {
        this.partitionedKeyValueApi.put(key, value);
        return "COMMIT";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/kv/{key}")
    public byte[] getOuter(@PathVariable final String key) {
        Optional<KeyValueRecord> record = this.partitionedKeyValueApi.get(key);
        if (record.isPresent()) {
            return record.get().getData();
        }

        throw new NoSuchElementException();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/kv")
    public Set<String> getKeys(@RequestParam("prefix") String prefix) {
        return partitionedKeyValueApi.getKeys(prefix);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/kv/{key}")
    public void delete(@PathVariable final String key) {
        partitionedKeyValueApi.delete(key);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/info")
    public Set<NodeInfo> getInfo() {
        return partitionedKeyValueApi.getInfo();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle(NoSuchElementException e) {
        return Optional.ofNullable(e.getMessage()).orElse("");
    }
}
