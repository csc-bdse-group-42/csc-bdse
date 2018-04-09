package ru.csc.bdse.resolver;

import com.google.common.collect.Sets;
import ru.csc.bdse.model.KeyValueRecord;

import java.util.*;
import java.util.stream.Collectors;

public class Resolver implements ConflictResolver {
    @Override
    public Optional<KeyValueRecord> resolve(Set<KeyValueRecord> allKeyValueRecords) {
        KeyValueRecord resultData;

        Set<KeyValueRecord> keyValueRecords = allKeyValueRecords.stream().filter(r -> !r.isDeleted()).collect(Collectors.toCollection(HashSet::new));

        Optional<KeyValueRecord> maxKeyValueRecord = keyValueRecords.stream().max(Comparator.comparing(KeyValueRecord::getTimestamp));
        if (maxKeyValueRecord.isPresent()) {
            long maxTime = maxKeyValueRecord.get().getTimestamp();
            Map<byte[], List<KeyValueRecord>> mapGroupByData = keyValueRecords.stream().
                    filter(t -> t.getTimestamp() == maxTime).
                    collect(Collectors.groupingBy(KeyValueRecord::getData));

            long countUniqueListSize = mapGroupByData.entrySet().stream().map(entry -> entry.getValue().size()).distinct().count();

            Optional<Map.Entry<byte[], List<KeyValueRecord>>> maxEntry;

            if (countUniqueListSize == 1) {
                maxEntry = mapGroupByData.entrySet().stream().
                        max(Comparator.comparing(entry -> entry.getKey().length));
            } else {
                maxEntry = mapGroupByData.entrySet().stream().
                        max(Comparator.comparing(entry -> entry.getValue().size()));
            }

            if (maxEntry.isPresent()) {
                resultData = maxEntry.get().getValue().get(0);

                return Optional.of(resultData);
            }
        }

        return Optional.empty();
    }

    @Override
    public Set<String> resolveKeys(Set<Set<String>> setOfSetKeys) {
        Set<String> result = new HashSet<>();

        return setOfSetKeys.stream().reduce(result, Sets::union);
    }
}
