package ru.csc.bdse.app.service;

import ru.csc.bdse.app.Record;

import java.util.Set;

public class BookRecordV2 implements Record{
    private String name;
    private String phone1;
    private String phone2;

    public BookRecordV2(String name, String phone1, String phone2) {
        this.name = name;
        this.phone1 = phone1;
        this.phone2 = phone2;
    }

    @Override
    public Set<Character> literals() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }
}
