package ru.csc.bdse.kv;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import ru.csc.bdse.model.KeyValueRecord;
import ru.csc.bdse.util.Env;

import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Test have to be implemented
 *
 * @author alesavin
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractPartitionedKeyValueApiHttpClientTest {
    protected abstract PartitionedKeyValueApi newCluster1();

    protected abstract PartitionedKeyValueApi newCluster2();

    protected Set<String> keys() {
        Random random = new Random();
        random.setSeed(42);
        return Stream.generate(
                () -> RandomStringUtils.random(10, 0, 0, true, true, null, random)
        ).limit(1000).collect(Collectors.toSet());
    }

    protected abstract float expectedKeysLossProportion();

    protected abstract float expectedUndeletedKeysProportion();

    private PartitionedKeyValueApi cluster1 = newCluster1();
    private PartitionedKeyValueApi cluster2 = newCluster2();

    private Set<String> keys = keys();

    @Test
    public void stage1put1000KeysAndReadItCorrectlyOnCluster1() throws InterruptedException {
        byte[] testValue = "put1000KeysAndReadItCorrectlyOnCluster1".getBytes();
        for (String key : keys) {
            cluster1.put(key, testValue);
        }

        for (String key : keys) {
            Optional<KeyValueRecord> keyValueRecord = cluster1.get(key);
            Assert.assertTrue(keyValueRecord.isPresent());
            Assert.assertArrayEquals(keyValueRecord.get().getData(), testValue);
        }
    }

    @Test
    public void stage2readKeysFromCluster2AndCheckLossProportion() {
        double present = 0;
        for (String key : cluster1.getKeys("")) {
            Optional<KeyValueRecord> keyValueRecord = cluster2.get(key);
            if (keyValueRecord.isPresent() && !keyValueRecord.get().isDeleted()) {
                present++;
            }
        }

        double proportion = 1 - (present / 1000);
        Assert.assertEquals(expectedKeysLossProportion(), proportion, 0.01);
    }

    @Test
    public void stage3deleteAllKeysFromCluster2() {
        for (String key : cluster2.getKeys("")) {
            cluster2.delete(key);
        }
    }

    @Test
    public void stage4readKeysFromCluster1AfterDeletionAtCluster2() {
        double present = 0;
        for (String key : cluster1.getKeys("")) {
            Optional<KeyValueRecord> keyValueRecord = cluster1.get(key);
            if (keyValueRecord.isPresent() && !keyValueRecord.get().isDeleted()) {
                present++;
            }
        }
        double proportion = present / 1000;
        Assert.assertEquals(expectedUndeletedKeysProportion(), proportion, 0.01);
    }

    protected static GenericContainer createContainer(String nodeName) {
        return new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromFile("target/bdse-kvnode-0.0.2-SNAPSHOT.jar", new File
                                ("../bdse-kvnode/target/bdse-kvnode-0.0.2-SNAPSHOT.jar"))
                        .withFileFromClasspath("Dockerfile", "kvnode/Dockerfile"))
                .withEnv(Env.KVNODE_NAME, nodeName)
                .withExposedPorts(8001)
                .withStartupTimeout(Duration.of(30, SECONDS));
    }

    protected static String getNodeUrl(GenericContainer node) {
        return "http://localhost:" + node.getMappedPort(8001);
    }
}


