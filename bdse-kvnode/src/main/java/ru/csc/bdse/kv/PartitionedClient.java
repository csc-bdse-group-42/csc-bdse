package ru.csc.bdse.kv;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ru.csc.bdse.model.KeyValueRecord;

import java.util.Set;

public interface PartitionedClient {
    @RequestLine("PUT /kv/{key}")
    String put(@Param("key") final String key, final byte[] value);

    @RequestLine("GET /kv/{key}")
    @Headers("Content-Type: application/json")
    KeyValueRecord get(@Param("key") final String key);

    @RequestLine("GET /kv?prefix={prefix}")
    Set<String> find(@Param("prefix") final String prefix);

    @RequestLine("DELETE /kv/{key}")
    void delete(@Param("key") final String key);

    @RequestLine("GET /info")
    Set<NodeInfo> getInfo();
}
