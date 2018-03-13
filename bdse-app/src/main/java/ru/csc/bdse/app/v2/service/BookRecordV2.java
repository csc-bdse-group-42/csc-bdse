package ru.csc.bdse.app.v2.service;

import ru.csc.bdse.app.Record;

import java.util.List;
import java.util.Set;

public class BookRecordV2 implements Record{
    private final String firstName;
    private final String secondName;
    private final String nickName;
    private final List<String> phones;

    public BookRecordV2(String firstName, String secondName, String nickName, List<String> phones) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.nickName = nickName;
        this.phones = phones;
    }

    @Override
    public Set<Character> literals() {
        return null;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getNickName() {
        return nickName;
    }

    public List<String> getPhones() {
        return phones;
    }
}
