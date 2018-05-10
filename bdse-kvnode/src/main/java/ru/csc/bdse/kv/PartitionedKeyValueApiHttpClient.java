package ru.csc.bdse.kv;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import ru.csc.bdse.model.KeyValueRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PartitionedKeyValueApiHttpClient implements KeyValueApi{
    private PartitionedClient client;

    public PartitionedKeyValueApiHttpClient(String baseUrl) {
        client = Feign.builder().decoder(new JacksonDecoder()).target(PartitionedClient.class, baseUrl);
    }

    public String put(String key, byte[] value) {
        return client.put(key, value);
    }

    public Optional<KeyValueRecord> get(String key) {
        return Optional.of(client.get(key));
    }

    public Set<String> getKeys(String prefix) {
        return client.find(prefix);
    }

    public void delete(String key) {
        client.delete(key);
    }

    public Set<NodeInfo> getInfo() {
        return client.getInfo();
    }

    @Override
    public void action(String node, NodeAction action) {
        throw new RuntimeException("not implemented");
    }

}
