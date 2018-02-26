package ru.csc.bdse.kv;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.model.KeyValueRecord;

import java.util.*;

@Service
public class BerkleyKeyValueApi implements KeyValueApi {
    private BerkleyDataSource berkleyDataSource;

    @Autowired
    BerkleyKeyValueApi(BerkleyDataSource berkleyDataSource) {
        this.berkleyDataSource = berkleyDataSource;
    }

    private PrimaryIndex<String, KeyValueRecord> getPrimaryIndex() {
        EntityStore store = berkleyDataSource.getStore();
        return store.getPrimaryIndex(String.class, KeyValueRecord.class);
    }

    /**
     * Puts value to the storage by specified key.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(String key, byte[] value) {
        KeyValueRecord record = new KeyValueRecord(key, value);
        getPrimaryIndex().put(record);
    }

    /**
     * Returns value associated with specified key.
     *
     * @param key
     */
    @Override
    public Optional<byte[]> get(String key) {
        KeyValueRecord record = getPrimaryIndex().get(key);
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(record.getData());
    }

    /**
     * Returns all keys with specified prefix.
     *
     * @param prefix
     */
    @Override
    public Set<String> getKeys(String prefix) {
        Set<String> keys = new TreeSet<>();
        try (EntityCursor<KeyValueRecord> cursor = getPrimaryIndex().entities()) {
            for (KeyValueRecord record: cursor) {
                if (record.getKey().startsWith(prefix)) {
                    keys.add(record.getKey());
                }
            }
        }
        return keys;
    }

    /**
     * Deletes value associated with specified key from the storage.
     *
     * @param key
     */
    @Override
    public void delete(String key) {
        try (EntityCursor<KeyValueRecord> cursor = getPrimaryIndex().entities()) {
            for (KeyValueRecord record: cursor) {
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
        return Collections.singleton(new NodeInfo(berkleyDataSource.getStore().getStoreName(), NodeStatus.UP));
    }

    /**
     * Do action on specified node.
     *
     * @param node
     * @param action
     */
    @Override
    public void action(String node, NodeAction action) {
        throw new RuntimeException("action not implemented now");
    }
}
