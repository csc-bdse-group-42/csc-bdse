package ru.csc.bdse.app.v2.service;

import ru.csc.bdse.app.Record;

import java.util.*;

public class BookRecordV2 implements Record {
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
        return new HashSet<>(Arrays.asList(secondName.charAt(0), nickName.charAt(0)));
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

    @Override
    public String toString() {
        return "BookRecordV2{" +
                "firstName='" + firstName + '\'' +
                ", secondName=" + secondName + '\'' +
                ", nickName=" + nickName + '\'' +
                ", phones=" + phones.toString() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRecordV2 that = (BookRecordV2) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(secondName, that.secondName) &&
                Objects.equals(nickName, that.nickName) &&
                phones.equals(that.phones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, secondName, nickName, phones);
    }
}
