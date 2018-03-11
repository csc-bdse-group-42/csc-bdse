package ru.csc.bdse.app.v2.service;

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
public class ServiceV2 implements PhoneBookApi<BookRecordV2> {
    private final BerkleyKeyValueApi berkleyKeyValueApi;

    @Autowired
    ServiceV2(BerkleyKeyValueApi berkleyKeyValueApi) {
        this.berkleyKeyValueApi = berkleyKeyValueApi;
    }


    @Override
    public void put(BookRecordV2 record) {
        Require.nonNull(record, "null key");

        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String nickName = record.getNickName();

        String phone1 = record.getPhone1();
        String phone2 = record.getPhone2();

        RecordBookProtos.AddressBook.Builder addressBook = RecordBookProtos.AddressBook.newBuilder();
        RecordBookProtos.Person.Builder person = RecordBookProtos.Person.newBuilder();
        person.setFirstName(firstName);
        person.setSecondName(secondName);
        person.setNickname(nickName);
        person.setPhones(0, phone1);
        person.setPhones(1, phone2);

        RecordBookProtos.Person p = person.build();
        addressBook.addPeople(p);

        byte[] byteRecord = addressBook.build().toByteArray();

        String id = firstName + secondName + UUID.randomUUID().toString();
        berkleyKeyValueApi.put(id, byteRecord);
    }

    @Override
    public void delete(BookRecordV2 record) {
        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String prefixId = firstName + secondName;

        Set<String> ids = berkleyKeyValueApi.getKeys(prefixId);

        for (String id : ids) {
            berkleyKeyValueApi.delete(id);
        }
    }

    @Override
    public Set<BookRecordV2> get(char literal) {
        Set<BookRecordV2> bookRecords = new HashSet<>();

        for (String key : berkleyKeyValueApi.getKeys(Character.toString(literal))) {
            Optional<byte[]> bytes = berkleyKeyValueApi.get(key);

            if (bytes.isPresent()) {
                try {
                    RecordBookProtos.AddressBook addressBook =
                            RecordBookProtos.AddressBook.parseFrom(new ByteArrayInputStream(bytes.get()));

                    for (RecordBookProtos.Person person : addressBook.getPeopleList()) {
                        BookRecordV2 recordV2 = new BookRecordV2(person.getFirstName(), person.getSecondName(),
                                person.getNickname(), person.getPhones(0), person.getPhones(1));
                        bookRecords.add(recordV2);
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException();
                }
            }

        }

        return bookRecords;
    }
}
