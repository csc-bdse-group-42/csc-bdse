package ru.csc.bdse.app.v11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.RecordBookProtos;
import ru.csc.bdse.app.util.SurnameCannotStartWithAtException;
import ru.csc.bdse.app.v11.model.BookRecordV11;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Require;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Primary
@Qualifier("PhoneBookServiceV11")
@Service
public class PhoneBookServiceV11 implements PhoneBookApi<BookRecordV11> {
    private final BerkleyKeyValueApi berkleyKeyValueApi;

    @Autowired
    public PhoneBookServiceV11(BerkleyKeyValueApi berkleyKeyValueApi) {
        this.berkleyKeyValueApi = berkleyKeyValueApi;
    }

    private String recordKey(BookRecordV11 record) {
        return record.getSecondName() + ';' + record.getFirstName();
    }

    @Override
    public void put(BookRecordV11 record) {
        Require.nonNull(record, "null key");

        String firstName = record.getFirstName();
        String secondName = record.getSecondName();
        String nickName = record.getNickName();

        if (secondName.startsWith("@")) {
            throw new SurnameCannotStartWithAtException();
        }

        List<String> phones = record.getPhones();

        RecordBookProtos.Person.Builder person = RecordBookProtos.Person.newBuilder();
        person.setFirstName(firstName);
        person.setSecondName(secondName);
        person.setNickname(nickName);

        for (String phone : phones) {
            person.addPhones(phone);
        }

        String key = recordKey(record);
        byte[] byteKey = key.getBytes();
        byte[] byteRecord = person.build().toByteArray();

        berkleyKeyValueApi.put("@" + nickName, byteKey);
        berkleyKeyValueApi.put(recordKey(record), byteRecord);
    }

    @Override
    public void delete(BookRecordV11 record) {
        berkleyKeyValueApi.delete("@" + record.getNickName());
        berkleyKeyValueApi.delete(recordKey(record));
    }

    @Override
    public Set<BookRecordV11> get(char literal) {
        Set<BookRecordV11> bookRecords = new HashSet<>();

        Set<String> surnameKeys = berkleyKeyValueApi.getKeys(Character.toString(literal));

        Set<String> nicknameKeys = berkleyKeyValueApi.getKeys("@" + literal);
        nicknameKeys.stream()
                    .map(berkleyKeyValueApi::get)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(bytes -> surnameKeys.add(new String(bytes)));

        for (String key : surnameKeys) {
            Optional<byte[]> bytes = berkleyKeyValueApi.get(key);

            if (bytes.isPresent()) {
                try {
                    RecordBookProtos.Person person = RecordBookProtos.Person.parseFrom(new ByteArrayInputStream(bytes.get()));
                    BookRecordV11 recordV2 = new BookRecordV11(person.getFirstName(), person.getSecondName(),
                                person.getNickname(), person.getPhonesList());
                    bookRecords.add(recordV2);
                } catch (IOException e) {
                    throw new IllegalArgumentException();
                }
            }
        }

        return bookRecords;
    }
}
