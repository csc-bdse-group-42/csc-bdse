package ru.csc.bdse.app.service;

import ru.csc.bdse.app.Record;

import java.util.Set;

public class BookRecordV1 implements Record {
    private String name;
    private String phone;

    @Override
    public Set<Character> literals() {
        return null;
    }

    public BookRecordV1(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
