package ru.csc.bdse.kv;

import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;
import ru.csc.bdse.partitioning.FirstLetterPartitioner;
import ru.csc.bdse.partitioning.ModNPartitioner;

import java.util.LinkedHashSet;
import java.util.Set;

public class PartitionSchemaChangeTest extends AbstractPartitionedKeyValueApiHttpClientTest {

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
        return new PartitionedKeyValueApi(nodes, 3, new FirstLetterPartitioner(nodes));
    }

    @Override
    protected PartitionedKeyValueApi newCluster2() {
        Set<String> nodes = new LinkedHashSet<>();
        nodes.add(getNodeUrl(node0));
        nodes.add(getNodeUrl(node1));
        nodes.add(getNodeUrl(node2));
        return new PartitionedKeyValueApi(nodes, 3, new ModNPartitioner(nodes));
    }

    @Override
    protected float expectedKeysLossProportion() {
        return 0.692f;
    }

    @Override
    protected float expectedUndeletedKeysProportion() {
        return 0.692f;
    }
}
