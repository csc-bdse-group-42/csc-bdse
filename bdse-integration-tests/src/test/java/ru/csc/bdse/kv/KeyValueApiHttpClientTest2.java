package ru.csc.bdse.kv;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import ru.csc.bdse.util.Env;
import ru.csc.bdse.util.IllegalNodeStateException;

import java.io.File;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Test have to be implemented
 *
 * @author alesavin
 */
public class KeyValueApiHttpClientTest2 {

    @ClassRule
    public static final GenericContainer node = new GenericContainer(
            new ImageFromDockerfile()
                    .withFileFromFile("target/bdse-kvnode-0.0.1-SNAPSHOT.jar", new File
                            ("../bdse-kvnode/target/bdse-kvnode-0.0.1-SNAPSHOT.jar"))
                    .withFileFromClasspath("Dockerfile", "kvnode/Dockerfile"))
            .withEnv(Env.KVNODE_NAME, "node-0")
            .withExposedPorts(8080)
            .withStartupTimeout(Duration.of(30, SECONDS));

    private KeyValueApi api = newKeyValueApi();

    private KeyValueApi newKeyValueApi() {
        final String baseUrl = "http://localhost:" + node.getMappedPort(8080);
        return new KeyValueApiHttpClient(baseUrl);
    }

    @Test
    public void concurrentPuts() {
        // TODO simultanious puts for the same key value
    }

    @Test
    public void concurrentDeleteAndKeys() {
        //TODO simultanious delete by key and keys listing
    }

    @Test
    public void actionUpDown() {
        NodeInfo ni = this.api.getInfo().iterator().next();
        Assert.assertEquals(ni.getStatus(), NodeStatus.UP);
        // Down node and check
        api.action(ni.getName(), NodeAction.DOWN);
        ni = this.api.getInfo().iterator().next();
        Assert.assertEquals(ni.getStatus(), NodeStatus.DOWN);
        // Up node and check
        api.action(ni.getName(), NodeAction.UP);
        ni = this.api.getInfo().iterator().next();
        Assert.assertEquals(ni.getStatus(), NodeStatus.UP);
    }

    @Test(expected = IllegalNodeStateException.class)
    public void putWithStoppedNode() {
        NodeInfo ni = this.api.getInfo().iterator().next();
        api.action(ni.getName(), NodeAction.DOWN);
        api.put("Foo", "bar".getBytes());
    }

    @Test(expected = IllegalNodeStateException.class)
    public void getWithStoppedNode() {
        NodeInfo ni = this.api.getInfo().iterator().next();
        api.action(ni.getName(), NodeAction.DOWN);
        api.put("Foo", "bar".getBytes());
    }

    @Test(expected = IllegalNodeStateException.class)
    public void getKeysByPrefixWithStoppedNode() {
        NodeInfo ni = this.api.getInfo().iterator().next();
        api.action(ni.getName(), NodeAction.DOWN);
        api.getKeys("F");
    }

    @Test
    public void deleteByTombstone() {
        // TODO use tombstones to mark as deleted (optional)
    }

    @Test
    public void loadMillionKeys()  {
        //TODO load too many data (optional)
    }
}


