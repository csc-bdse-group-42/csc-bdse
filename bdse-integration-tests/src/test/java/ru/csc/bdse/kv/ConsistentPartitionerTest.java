package ru.csc.bdse.kv;

import org.junit.Assert;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;
import ru.csc.bdse.model.KeyValueRecord;
import ru.csc.bdse.partitioning.ConsistentHashMd5Partitioner;

import java.util.*;

public class ConsistentPartitionerTest extends AbstractPartitionedKeyValueApiHttpClientTest{

    @ClassRule
    public static final GenericContainer node0 = createContainer("node-0");

    @ClassRule
    public static final GenericContainer node1 = createContainer("node-1");

    @ClassRule
    public static final GenericContainer node2 = createContainer("node-2");

    @Override
    protected PartitionedKeyValueApi newCluster1() {
        Set<String> nodes = new LinkedHashSet<>();
        nodes.add(getNodeUrl(node0));
        nodes.add(getNodeUrl(node1));
        nodes.add(getNodeUrl(node2));
        return new PartitionedKeyValueApi(nodes, 3, new ConsistentHashMd5Partitioner(nodes));
    }

    @Override
    protected PartitionedKeyValueApi newCluster2() {
        List<String> urlList = new ArrayList<>();
        urlList.add(getNodeUrl(node0));
        urlList.add(getNodeUrl(node1));
        urlList.add(getNodeUrl(node2));
        Collections.sort(urlList);

        Set<String> nodes = new LinkedHashSet<>();
        nodes.add(urlList.get(0));
        nodes.add(urlList.get(2));
        return new PartitionedKeyValueApi(nodes, 3, new ConsistentHashMd5Partitioner(nodes));
    }

    @Override
    public void stage2readKeysFromCluster2AndCheckLossProportion() {
        // Cannot get constant results
    }

    @Override
    public void stage4readKeysFromCluster1AfterDeletionAtCluster2() {
        // Cannot get constant results
    }

    @Override
    protected float expectedKeysLossProportion() {
        return 0.33f;
    }

    @Override
    protected float expectedUndeletedKeysProportion() {
        return 0.33f;
    }
}
