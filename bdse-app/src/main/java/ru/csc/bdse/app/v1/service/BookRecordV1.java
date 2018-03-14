package ru.csc.bdse.app.v1.service;

import ru.csc.bdse.app.Record;

import java.util.HashSet;
import java.util.Set;

public class BookRecordV1 implements Record {
    private String firstName;
    private String secondName;
    private String phone;

    @Override
    public Set<Character> literals() {
        return new HashSet<>(secondName.charAt(0));
    }

    public BookRecordV1(String firstName, String secondName, String phone) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.phone = phone;
    }

    public BookRecordV1(String firstName, String secondName) {
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPhone() {
        return phone;
    }
}
