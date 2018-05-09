package ru.csc.bdse.kv;

import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;
import ru.csc.bdse.partitioning.FirstLetterPartitioner;
import ru.csc.bdse.partitioning.ModNPartitioner;

import java.util.*;

public class ModNPartitionerTest extends AbstractPartitionedKeyValueApiHttpClientTest {

    @ClassRule
    public static final GenericContainer node0 = createContainer("node-0");

    @ClassRule
    public static final GenericContainer node1 = createContainer("node-1");

    @ClassRule
    public static final GenericContainer node2 = createContainer("node-2");

    @ClassRule
    public static final GenericContainer node3 = createContainer("node-3");

    @ClassRule
    public static final GenericContainer node4 = createContainer("node-4");

    @Override
    protected PartitionedKeyValueApi newCluster1() {
        Set<String> nodes = new LinkedHashSet<>();
        nodes.add(getNodeUrl(node0));
        nodes.add(getNodeUrl(node1));
        nodes.add(getNodeUrl(node2));
        nodes.add(getNodeUrl(node3));
        nodes.add(getNodeUrl(node4));
        return new PartitionedKeyValueApi(nodes, 3, new ModNPartitioner(nodes));
    }

    @Override
    protected PartitionedKeyValueApi newCluster2() {
        List<String> urlList = new ArrayList<>();
        urlList.add(getNodeUrl(node0));
        urlList.add(getNodeUrl(node1));
        urlList.add(getNodeUrl(node2));
        urlList.add(getNodeUrl(node3));
        urlList.add(getNodeUrl(node4));
        Collections.sort(urlList);

        Set<String> nodes = new LinkedHashSet<>();
        nodes.add(urlList.get(0));
        nodes.add(urlList.get(1));
        nodes.add(urlList.get(2));
        return new PartitionedKeyValueApi(nodes, 3, new ModNPartitioner(nodes));
    }

    @Override
    protected float expectedKeysLossProportion() {
        return 0.825f;
    }

    @Override
    protected float expectedUndeletedKeysProportion() {
        return 0.825f;
    }
}
