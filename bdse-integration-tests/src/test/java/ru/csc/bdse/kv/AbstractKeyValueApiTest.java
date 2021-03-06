package ru.csc.bdse.kv;

import org.assertj.core.api.SoftAssertions;
import org.junit.Ignore;
import org.junit.Test;
import ru.csc.bdse.model.KeyValueRecord;
import ru.csc.bdse.util.Constants;
import ru.csc.bdse.util.Random;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author semkagtn
 */
public abstract class AbstractKeyValueApiTest {

    protected abstract KeyValueApi newKeyValueApi();

    private KeyValueApi api = newKeyValueApi();

    @Test
    public void createValue() {
        SoftAssertions softAssert = new SoftAssertions();

        String key = Random.nextKey();
        byte[] value = Random.nextValue();

        Optional<KeyValueRecord> oldValue = api.get(key);
        softAssert.assertThat(oldValue.isPresent()).as("old value").isFalse();

        api.put(key, value);
        KeyValueRecord newValue = api.get(key).orElse(null);
        assertThat(newValue.getData()).as("new value").isEqualTo(value);

        softAssert.assertAll();
    }

    @Test
    public void updateValue() {
        SoftAssertions softAssert = new SoftAssertions();

        String key = Random.nextKey();
        byte[] oldValue = Random.nextValue();
        byte[] newValue = Random.nextValue();

        api.put(key, oldValue);
        KeyValueRecord actualOldValue = api.get(key).orElse(null);
        softAssert.assertThat(actualOldValue.getData()).as("old value").isEqualTo(oldValue);

        api.put(key, newValue);
        KeyValueRecord actualNewValue = api.get(key).orElse(null);
        softAssert.assertThat(actualNewValue.getData()).as("new value").isEqualTo(newValue);

        softAssert.assertAll();
    }

    @Test
    public void deleteValue() {
        SoftAssertions softAssert = new SoftAssertions();

        String key = Random.nextKey();
        byte[] value = Random.nextValue();

        api.put(key, value);
        KeyValueRecord actualOldValue = api.get(key).orElse(null);
        softAssert.assertThat(actualOldValue.getData()).as("old value").isEqualTo(value);

        api.delete(key);
        Optional<KeyValueRecord> actualNewValue = api.get(key);
        softAssert.assertThat(actualNewValue.isPresent() && !actualNewValue.get().isDeleted()).as("new value").isFalse();

        softAssert.assertAll();
    }

    @Test
    public void deleteNonexistentValue() {
        SoftAssertions softAssert = new SoftAssertions();

        String nonexistentKey = Random.nextKey();
        Optional<KeyValueRecord> actualOldValue = api.get(nonexistentKey);
        softAssert.assertThat(actualOldValue.isPresent() && !actualOldValue.get().isDeleted()).as("old value").isFalse();

        api.delete(nonexistentKey);
        Optional<KeyValueRecord> actualNewValue = api.get(nonexistentKey);
        softAssert.assertThat(actualNewValue.isPresent() && !actualNewValue.get().isDeleted()).as("new value").isFalse();

        softAssert.assertAll();
    }

    @Test
    public void getClusterInfoValue() {
        SoftAssertions softAssert = new SoftAssertions();

        Set<NodeInfo> info = api.getInfo();
        softAssert.assertThat(info).as("size").hasSize(1);
        softAssert.assertThat(info.iterator().next().getStatus()).as("status").isEqualTo(NodeStatus.UP);

        softAssert.assertAll();
    }

    @Test
    public void getKeysByPrefix() {
        SoftAssertions softAssert = new SoftAssertions();

        String prefix1 = "prefix1";
        String key1 = prefix1 + Random.nextKey();
        String key2 = prefix1 + Random.nextKey();
        Set<String> prefix1Keys = new HashSet<>();
        prefix1Keys.add(key1);
        prefix1Keys.add(key2);

        String prefix2 = "prefix2";
        String key3 = prefix2 + Random.nextKey();
        Set<String> prefix2Keys = Collections.singleton(key3);
        byte[] value = Random.nextValue();

        api.put(key1, value);
        api.put(key2, value);
        api.put(key3, value);

        Set<String> actualPrefix1Keys = api.getKeys(prefix1);
        softAssert.assertThat(actualPrefix1Keys).as("prefix1").isEqualTo(prefix1Keys);

        Set<String> actualPrefix2Keys = api.getKeys(prefix2);
        softAssert.assertThat(actualPrefix2Keys).as("prefix2").isEqualTo(prefix2Keys);

        softAssert.assertAll();
    }
}
