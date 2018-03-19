package ru.csc.bdse.app.model;

import ru.csc.bdse.app.Record;

import java.awt.print.Book;
import java.util.*;

public class BookRecord implements Record {
    private String firstName;
    private String secondName;
    private String nickName;
    private List<String> phones;

    public BookRecord(String firstName, String secondName, String nickName, List<String> phones) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.nickName = nickName;
        this.phones = phones;
    }

    public BookRecord() {}

    public BookRecord(String firstName, String secondName) {
        this.firstName = firstName;
        this.secondName = secondName;
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
        return "BookRecord{" +
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
        BookRecord that = (BookRecord) o;
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
