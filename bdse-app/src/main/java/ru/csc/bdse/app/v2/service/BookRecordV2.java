package ru.csc.bdse.app.v2.service;

import ru.csc.bdse.app.Record;

import java.util.Set;

public class BookRecordV2 implements Record{
    private final String firstName;
    private final String secondName;
    private final String nickName;
    private final String phone1;
    private final String phone2;

    public BookRecordV2(String firstName, String secondName, String nickName, String phone1, String phone2) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.nickName = nickName;
        this.phone1 = phone1;
        this.phone2 = phone2;
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

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }
}
