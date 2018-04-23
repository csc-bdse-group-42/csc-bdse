package ru.csc.bdse.partitioning;

import ru.csc.bdse.kv.KeyValueApi;
import ru.csc.bdse.kv.KeyValueApiHttpClient;

import java.util.List;

public class PartitionCoordinator {
    private List<KeyValueApi> partitions;
    private int timeout;
    private Partitioner partitioner;

    public PartitionCoordinator(List<KeyValueApi> partitions, Partitioner partitioner, int timeout) {
        this.partitions = partitions;
        this.timeout = timeout;
        this.partitioner = partitioner;
    }

    public KeyValueApi getPartition(String key) {
        return new KeyValueApiHttpClient(partitioner.getPartition(key));
    }

    public void put(String key, byte[] value) {
        this.getPartition(key).put(key, value);
    }

}
