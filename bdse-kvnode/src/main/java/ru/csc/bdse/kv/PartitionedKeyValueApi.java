package ru.csc.bdse.kv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.ApplicationProperties;
import ru.csc.bdse.model.KeyValueRecord;
import ru.csc.bdse.partitioning.PartitionCoordinator;
import ru.csc.bdse.partitioning.Partitioner;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class PartitionedKeyValueApi {

    private PartitionCoordinator coordinator;
    private List<KeyValueApi> partitionList;

    /**
     * @throws ClassNotFoundException - if partitioner from properties not found
     * @throws NoSuchMethodException - if partitioner hasn't constructor from Set<String>
     * @throws InvocationTargetException - if partitioner constructor throws exception
     * @throws InstantiationException - if partitioner cannot be instantiated
     * @throws IllegalAccessException - if partitioner constructor is private
     */
    @Autowired
    PartitionedKeyValueApi(ApplicationProperties applicationProperties) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int timeout = applicationProperties.getNodeTimeout();

        partitionList = new ArrayList<>();
        for (String partitionUrl : applicationProperties.getPartitions()) {
            partitionList.add(new KeyValueApiHttpClient(partitionUrl));
        }
        Partitioner partitioner = applicationProperties.getPartitioner();

        this.coordinator = new PartitionCoordinator(partitionList, partitioner, timeout);
    }

    PartitionedKeyValueApi(Set<String> partitionUrls, int timeout, Partitioner partitioner) {
        List<String> urlList = new ArrayList<>(partitionUrls);
        Collections.sort(urlList);

        partitionList = new ArrayList<>();
        for (String partitionUrl : urlList) {
            partitionList.add(new KeyValueApiHttpClient(partitionUrl));
        }
        this.coordinator = new PartitionCoordinator(partitionList, partitioner, timeout);
    }

    public void put(String key, byte[] value) {
        coordinator.put(key, value);
    }

    public Optional<KeyValueRecord> get(String key) {
        try {
            return coordinator.get(key);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return Optional.empty();
        }
    }

    public Set<String> getKeys(String prefix) {
        return coordinator.getKeys(prefix);
    }

    public void delete(String key) {
        coordinator.delete(key);
    }

    public Set<NodeInfo> getInfo() {
        return partitionList.stream()
                .map(KeyValueApi::getInfo)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void action(String node, NodeAction action) { }
}
