package ru.csc.bdse.kv;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ru.csc.bdse.model.KeyValueRecord;

interface NodeClient {
    @RequestLine("PUT /key-value-inner/{key}")
    String putInner(@Param("key") final String key, final byte[] value);

    @RequestLine("GET /key-value-inner/{key}")
    @Headers("Content-Type: application/json")
    KeyValueRecord getInner(@Param("key") final String key);
}