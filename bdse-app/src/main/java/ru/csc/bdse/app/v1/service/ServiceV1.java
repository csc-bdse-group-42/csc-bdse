package ru.csc.bdse.app.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.RecordBookProtos;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Require;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ServiceV1 implements PhoneBookApi<BookRecordV1> {
    private final BerkleyKeyValueApi berkleyKeyValueApi;

    @Autowired
    public ServiceV1(BerkleyKeyValueApi berkleyKeyValueApi) {
        this.berkleyKeyValueApi = berkleyKeyValueApi;
    }


    @Override
    public void put(BookRecordV1 record) {
        Require.nonNull(record, "null key");

        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String phone = record.getPhone();

        RecordBookProtos.Person.Builder person = RecordBookProtos.Person.newBuilder();
        person.setFirstName(firstName);
        person.setSecondName(secondName);
        person.addPhones(phone);

        byte[] byteRecord = person.build().toByteArray();
        String id = secondName + UUID.randomUUID().toString();
        berkleyKeyValueApi.put(id, byteRecord);
    }

    @Override
    public void delete(BookRecordV1 record) {
        String secondName = record.getSecondName();
        Set<String> ids = berkleyKeyValueApi.getKeys(secondName);

        for (String id : ids) {
            berkleyKeyValueApi.delete(id);
        }
    }

    @Override
    public Set<BookRecordV1> get(char literal) {
        Set<BookRecordV1> bookRecords = new HashSet<>();

        for (String key : berkleyKeyValueApi.getKeys(Character.toString(literal))) {
            Optional<byte[]> bytes = berkleyKeyValueApi.get(key);

            if (bytes.isPresent()) {
                try {
                    RecordBookProtos.Person person = RecordBookProtos.Person.parseFrom(new ByteArrayInputStream(bytes.get()));
                    BookRecordV1 recordV1 = new BookRecordV1(person.getFirstName(), person.getSecondName(), person.getPhones(0));
                    bookRecords.add(recordV1);
                } catch (IOException e) {
                    throw new IllegalArgumentException();
                }
            }

        }

        return bookRecords;
    }
}
