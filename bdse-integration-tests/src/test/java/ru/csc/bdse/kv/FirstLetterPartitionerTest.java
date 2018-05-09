package ru.csc.bdse.kv;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import ru.csc.bdse.partitioning.FirstLetterPartitioner;
import ru.csc.bdse.util.Env;

import java.io.File;
import java.time.Duration;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;

public class FirstLetterPartitionerTest extends AbstractPartitionedKeyValueApiHttpClientTest {

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
        List<String> urlList = new ArrayList<>();
        urlList.add(getNodeUrl(node0));
        urlList.add(getNodeUrl(node1));
        urlList.add(getNodeUrl(node2));
        Collections.sort(urlList);

        Set<String> nodes = new LinkedHashSet<>();
        nodes.add(urlList.get(0));
        nodes.add(urlList.get(2));
        return new PartitionedKeyValueApi(nodes, 3, new FirstLetterPartitioner(nodes));
    }

    @Override
    protected float expectedKeysLossProportion() {
        return 0;
    }

    @Override
    protected float expectedUndeletedKeysProportion() {
        return 0;
    }
}
