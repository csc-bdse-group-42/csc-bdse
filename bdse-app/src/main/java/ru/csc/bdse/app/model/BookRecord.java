package ru.csc.bdse.app.model;

import ru.csc.bdse.app.Record;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BookRecord implements Record {
    private String firstName;
    private String secondName;
    private String phone;

    @Override
    public Set<Character> literals() {
        return new HashSet<>(Collections.singletonList(secondName.charAt(0)));
    }

    public BookRecord() {}

    public BookRecord(String firstName, String secondName, String phone) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.phone = phone;
    }

    public BookRecord(String firstName, String secondName) {
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

    @Override
    public String toString() {
        return "BookRecord{" +
                "firstName='" + firstName + '\'' +
                ", secondName=" + secondName + '\'' +
                ", phone=" + phone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRecord that = (BookRecord) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(secondName, that.secondName) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, secondName, phone);
    }
}
