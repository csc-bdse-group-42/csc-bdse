package ru.csc.bdse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.RecordBookProtos;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Require;

import java.util.Optional;
import java.util.Set;

@Service
public class ServiceV1 implements PhoneBookApi<BookRecordV1>{
    private final BerkleyKeyValueApi berkleyKeyValueApi;

    @Autowired
    ServiceV1(BerkleyKeyValueApi berkleyKeyValueApi) {
        this.berkleyKeyValueApi = berkleyKeyValueApi;
    }


    @Override
    public void put(BookRecordV1 record) {
        Require.nonNull(record, "null key");

        String name = record.getName();
        String phone = record.getPhone();

        RecordBookProtos.AddressBook.Builder addressBook = RecordBookProtos.AddressBook.newBuilder();
        RecordBookProtos.Person.Builder person = RecordBookProtos.Person.newBuilder();
        person.setFirstName(name);
        person.setPhones(0, phone);

        RecordBookProtos.Person p = person.build();

        addressBook.addPeople(p);

        byte[] bytes = addressBook.build().toByteArray();

        berkleyKeyValueApi.put(name, bytes);
    }

    @Override
    public void delete(BookRecordV1 record) {
        String name = record.getName();
        berkleyKeyValueApi.delete(name);
    }

    @Override
    public Set<BookRecordV1> get(char literal) {
        for (String key : berkleyKeyValueApi.getKeys(Character.toString(literal))){
            Optional<byte[]> bytes = berkleyKeyValueApi.get(key);

            System.out.println(bytes);
        }

        return null;
    }
}
