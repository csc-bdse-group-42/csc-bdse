package ru.csc.bdse.kv;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestParam;
import ru.csc.bdse.model.KeyValueRecord;

import java.util.Set;

interface NodeClient {
    @RequestLine("PUT /key-value-inner/{key}")
    String putInner(@Param("key") final String key, final byte[] value);

    @RequestLine("GET /key-value-inner/{key}")
    @Headers("Content-Type: application/json")
    KeyValueRecord getInner(@Param("key") final String key);

    @RequestLine("GET /key-value-inner/")
    @Headers("Content-Type: application/json")
    Set<String> getsInner(@RequestParam("prefix") final String prefix);
}