package ru.csc.bdse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.RecordBookProtos;
import ru.csc.bdse.app.model.BookRecord;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Require;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PhoneBookService implements PhoneBookApi<BookRecord> {
    private final BerkleyKeyValueApi berkleyKeyValueApi;

    @Autowired
    public PhoneBookService(BerkleyKeyValueApi berkleyKeyValueApi) {
        this.berkleyKeyValueApi = berkleyKeyValueApi;
    }

    private String recordKey(BookRecord record) {
        return record.getSecondName() + record.getFirstName();
    }

    @Override
    public void put(BookRecord record) {
        Require.nonNull(record, "null key");

        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String phone = record.getPhone();

        RecordBookProtos.Person.Builder person = RecordBookProtos.Person.newBuilder();
        person.setFirstName(firstName);
        person.setSecondName(secondName);
        person.addPhones(phone);

        byte[] byteRecord = person.build().toByteArray();
        berkleyKeyValueApi.put(recordKey(record), byteRecord);
    }

    @Override
    public void delete(BookRecord record) {
        berkleyKeyValueApi.delete(recordKey(record));
    }

    @Override
    public Set<BookRecord> get(char literal) {
        Set<BookRecord> bookRecords = new HashSet<>();

        for (String key : berkleyKeyValueApi.getKeys(Character.toString(literal))) {
            Optional<byte[]> bytes = berkleyKeyValueApi.get(key);

            if (bytes.isPresent()) {
                try {
                    RecordBookProtos.Person person = RecordBookProtos.Person.parseFrom(new ByteArrayInputStream(bytes.get()));
                    BookRecord recordV1 = new BookRecord(person.getFirstName(), person.getSecondName(), person.getPhones(0));
                    bookRecords.add(recordV1);
                } catch (IOException e) {
                    throw new IllegalArgumentException();
                }
            }
        }

        return bookRecords;
    }
}
