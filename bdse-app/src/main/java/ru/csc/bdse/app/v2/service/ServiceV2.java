package ru.csc.bdse.app.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.RecordBookProtos;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Require;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ServiceV2 implements PhoneBookApi<BookRecordV2> {
    private final BerkleyKeyValueApi berkleyKeyValueApi;

    @Autowired
    public ServiceV2(BerkleyKeyValueApi berkleyKeyValueApi) {
        this.berkleyKeyValueApi = berkleyKeyValueApi;
    }


    @Override
    public void put(BookRecordV2 record) {
        Require.nonNull(record, "null key");

        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String nickName = record.getNickName();

        List<String> phones = record.getPhones();

        RecordBookProtos.AddressBook.Builder addressBook = RecordBookProtos.AddressBook.newBuilder();
        RecordBookProtos.Person.Builder person = RecordBookProtos.Person.newBuilder();
        person.setFirstName(firstName);
        person.setSecondName(secondName);
        person.setNickname(nickName);

        for (String phone : phones) {
            person.addPhones(phone);
        }

        RecordBookProtos.Person p = person.build();
        addressBook.addPeople(p);

        byte[] byteRecord = addressBook.build().toByteArray();

        String id = secondName + UUID.randomUUID().toString();
        berkleyKeyValueApi.put(id, byteRecord);
    }

    @Override
    public void delete(BookRecordV2 record) {
        String secondName = record.getSecondName();
        Set<String> ids = berkleyKeyValueApi.getKeys(secondName);

        for (String id : ids) {
            berkleyKeyValueApi.delete(id);
        }
    }

    @Override
    public Set<BookRecordV2> get(char literal) {
        Set<BookRecordV2> bookRecords = new HashSet<>();

        for (String key : berkleyKeyValueApi.getKeys("")) {
            Optional<byte[]> bytes = berkleyKeyValueApi.get(key);

            if (bytes.isPresent()) {
                try {
                    RecordBookProtos.AddressBook addressBook =
                            RecordBookProtos.AddressBook.parseFrom(new ByteArrayInputStream(bytes.get()));

                    for (RecordBookProtos.Person person : addressBook.getPeopleList()) {
                        if ((person.hasSecondName() && person.getSecondName().charAt(0) == literal) ||
                                (person.hasNickname() && person.getNickname().charAt(0) == literal)) {
                            BookRecordV2 recordV2 = new BookRecordV2(person.getFirstName(), person.getSecondName(),
                                    person.getNickname(), person.getPhonesList());
                            bookRecords.add(recordV2);
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException();
                }
            }

        }

        return bookRecords;
    }
}
