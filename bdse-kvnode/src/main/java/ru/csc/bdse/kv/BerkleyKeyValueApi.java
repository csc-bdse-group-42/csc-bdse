package ru.csc.bdse.kv;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.model.KeyValueRecord;
import ru.csc.bdse.util.Require;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BerkleyKeyValueApi implements KeyValueApi {
    private final BerkleyDataSource berkleyDataSource;
    private String nodeName;

    @Autowired
    BerkleyKeyValueApi(BerkleyDataSource berkleyDataSource) {
        this.nodeName = "UNKNOWN_NODE";
        this.berkleyDataSource = berkleyDataSource;
    }

    private PrimaryIndex<String, KeyValueRecord> getPrimaryIndex() {
        EntityStore store = berkleyDataSource.getStore();
        return store.getPrimaryIndex(String.class, KeyValueRecord.class);
    }

    /**
     * Puts value to the storage by specified key.
     */
    @Override
    public void put(String key, byte[] value) {
        Require.nonNull(key, "null key");
        Require.nonNull(value, "null value");

        KeyValueRecord record = new KeyValueRecord(key, value);
        PrimaryIndex<String, KeyValueRecord> primaryIndex = getPrimaryIndex();
        primaryIndex.put(record);
    }

    /**
     * Returns value associated with specified key.
     */
    @Override
    public Optional<byte[]> get(String key) {
        Require.nonNull(key, "null key");

        PrimaryIndex<String, KeyValueRecord> primaryIndex = getPrimaryIndex();
        KeyValueRecord record = primaryIndex.get(key);
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(record.getData());
    }

    /**
     * Returns all keys with specified prefix.
     */
    @Override
    public Set<String> getKeys(String prefix) {
        Set<String> keys = new HashSet<>();
        PrimaryIndex<String, KeyValueRecord> primaryIndex = getPrimaryIndex();
        try (EntityCursor<KeyValueRecord> cursor = primaryIndex.entities()) {
            for (KeyValueRecord record : cursor) {
                if (record.getKey().startsWith(prefix)) {
                    keys.add(record.getKey());
                }
            }
        }
        return keys;
    }

    /**
     * Deletes value associated with specified key from the storage.
     */
    @Override
    public void delete(String key) {
        Require.nonNull(key, "null key");

        PrimaryIndex<String, KeyValueRecord> primaryIndex = getPrimaryIndex();
        try (EntityCursor<KeyValueRecord> cursor = primaryIndex.entities()) {
            for (KeyValueRecord record : cursor) {
                if (record.getKey().equals(key)) {
                    cursor.delete();
                }
            }
        }
    }

    /**
     * Returns info about all nodes.
     */
    @Override
    public Set<NodeInfo> getInfo() {
        return Collections.singleton(new NodeInfo(this.nodeName, NodeStatus.UP));
    }

    /**
     * Do action on specified node.
     */
    @Override
    public void action(String node, NodeAction action) {
        throw new IllegalArgumentException("Action is not implemented now");
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
