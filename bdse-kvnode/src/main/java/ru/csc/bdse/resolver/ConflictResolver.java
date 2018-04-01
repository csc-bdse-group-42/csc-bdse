package ru.csc.bdse.resolver;

import ru.csc.bdse.model.KeyValueRecord;

import java.util.Optional;
import java.util.Set;

public interface ConflictResolver {
    Optional<KeyValueRecord> resolve(Set<KeyValueRecord> keyValueRecords);

    Set<String> resolveKeys(Set<Set<String>> in);
}
