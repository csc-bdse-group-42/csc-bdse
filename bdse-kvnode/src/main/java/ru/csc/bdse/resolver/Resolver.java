package ru.csc.bdse.resolver;

import ru.csc.bdse.model.KeyValueRecord;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class Resolver implements ConflictResolver {
    @Override
    public Optional<KeyValueRecord> resolve(List<KeyValueRecord> allKeyValueRecords) {
        Set<KeyValueRecord> keyValueRecords = allKeyValueRecords.stream().filter(r -> !r.isDeleted()).collect(Collectors.toCollection(HashSet::new));

        Optional<KeyValueRecord> maxKeyValueRecord = keyValueRecords.stream().max(Comparator.comparing(KeyValueRecord::getTimestamp));


        if (maxKeyValueRecord.isPresent()) {
            long maxTime = maxKeyValueRecord.get().getTimestamp();
            long elementsWithMaxTime = keyValueRecords.stream().filter(t -> t.getTimestamp() == maxTime).count();

            if (elementsWithMaxTime > 1) {
                final Map<KeyValueRecord, Long> frequencies = keyValueRecords.stream()
                        .filter(t -> t.getTimestamp() == maxTime)
                        .collect(Collectors.groupingBy(Function.identity(), counting()));

                final Comparator<Map.Entry<KeyValueRecord, Long>> comparator = Comparator
                        .comparingLong((Map.Entry<KeyValueRecord, Long> e) -> e.getKey().getTimestamp())
                        .thenComparingLong(Map.Entry::getValue)
                        .thenComparing((Map.Entry<KeyValueRecord, Long> e) -> e.getKey().getKey());

                return Optional.of(frequencies.entrySet().stream().max(comparator).orElseThrow(IllegalStateException::new).getKey());

            } else {
                return maxKeyValueRecord;
            }
        }

        return Optional.empty();
    }


    @Override
    public Set<String> resolveKeys(List<Set<String>> setOfSetKeys) {
        Set<String> result = new HashSet<>();
        setOfSetKeys.forEach(result::addAll);

        return result;
    }
}
