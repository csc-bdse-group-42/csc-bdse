package ru.csc.bdse.kv;

import java.util.Optional;
import java.util.Set;

public class BerkleyKeyValueApi implements KeyValueApi{
    /**
     * Puts value to the storage by specified key.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(String key, byte[] value) {

    }

    /**
     * Returns value associated with specified key.
     *
     * @param key
     */
    @Override
    public Optional<byte[]> get(String key) {
        return Optional.empty();
    }

    /**
     * Returns all keys with specified prefix.
     *
     * @param prefix
     */
    @Override
    public Set<String> getKeys(String prefix) {
        return null;
    }

    /**
     * Deletes value associated with specified key from the storage.
     *
     * @param key
     */
    @Override
    public void delete(String key) {

    }

    /**
     * Returns info about all nodes.
     */
    @Override
    public Set<NodeInfo> getInfo() {
        return null;
    }

    /**
     * Do action on specified node.
     *
     * @param node
     * @param action
     */
    @Override
    public void action(String node, NodeAction action) {

    }
}
