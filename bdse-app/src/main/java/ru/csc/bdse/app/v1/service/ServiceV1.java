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
    ServiceV1(BerkleyKeyValueApi berkleyKeyValueApi) {
        this.berkleyKeyValueApi = berkleyKeyValueApi;
    }


    @Override
    public void put(BookRecordV1 record) {
        Require.nonNull(record, "null key");

        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String phone = record.getPhone();

        RecordBookProtos.AddressBook.Builder addressBook = RecordBookProtos.AddressBook.newBuilder();
        RecordBookProtos.Person.Builder person = RecordBookProtos.Person.newBuilder();
        person.setFirstName(firstName);
        person.setSecondName(secondName);
        person.setPhones(0, phone);

        RecordBookProtos.Person p = person.build();
        addressBook.addPeople(p);

        byte[] byteRecord = addressBook.build().toByteArray();
        String id = firstName + secondName + UUID.randomUUID().toString();
        berkleyKeyValueApi.put(id, byteRecord);
    }

    @Override
    public void delete(BookRecordV1 record) {
        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String prefixId = firstName + secondName;

        Set<String> ids = berkleyKeyValueApi.getKeys(prefixId);

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
                    RecordBookProtos.AddressBook addressBook =
                            RecordBookProtos.AddressBook.parseFrom(new ByteArrayInputStream(bytes.get()));

                    for (RecordBookProtos.Person person : addressBook.getPeopleList()) {
                        BookRecordV1 recordV1 = new BookRecordV1(person.getFirstName(), person.getSecondName(), person.getPhones(0));
                        bookRecords.add(recordV1);
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException();
                }
            }

        }

        return bookRecords;
    }
}
