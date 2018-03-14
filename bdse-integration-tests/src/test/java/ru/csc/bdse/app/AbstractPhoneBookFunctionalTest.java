package ru.csc.bdse.app;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import ru.csc.bdse.util.Random;

public abstract class AbstractPhoneBookFunctionalTest<T extends Record> {

    protected abstract PhoneBookApi<T> newPhoneBookApi();
    private PhoneBookApi<T> api = newPhoneBookApi();

    protected abstract T getNextRandomRecord();
    protected abstract T getNextRandomRecordWith(String firstName, String secondName);

    // Gets records from an empty phone book.
    @Test
    public void getFromEmptyBook() {
        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(api.get(Random.nextChar())).isEmpty();

        softAssert.assertAll();
    }

    // Puts and gets some test records.
    @Test
    public void putAndGet() {
        SoftAssertions softAssert = new SoftAssertions();

        T firstRecord = getNextRandomRecord();
        T secondRecord = getNextRandomRecord();

        api.put(firstRecord);
        for (char literal: firstRecord.literals()) {
            System.out.println(literal);
            softAssert.assertThat(api.get(literal).contains(firstRecord)).isTrue();
        }
        for (char literal: secondRecord.literals()) {
            softAssert.assertThat(api.get(literal).contains(secondRecord)).isFalse();
        }

        api.put(secondRecord);
        for (char literal: firstRecord.literals()) {
            softAssert.assertThat(api.get(literal).contains(firstRecord)).isTrue();
        }
        for (char literal: secondRecord.literals()) {
            softAssert.assertThat(api.get(literal).contains(secondRecord)).isTrue();
        }

        softAssert.assertAll();
    }

    // Puts and erases a test record.
    @Test
    public void erasure() {
        SoftAssertions softAssert = new SoftAssertions();

        T record = getNextRandomRecord();

        api.put(record);
        for (char literal: record.literals()) {
            softAssert.assertThat(api.get(literal).contains(record)).isTrue();
        }

        api.delete(record);
        for (char literal: record.literals()) {
            softAssert.assertThat(api.get(literal).contains(record)).isFalse();
        }

        softAssert.assertAll();
    }

    // Updates data.
    @Test
    public void update() {
        SoftAssertions softAssert = new SoftAssertions();

        String firstName = Random.nextKey();
        String secondName = Random.nextKey();

        T record = getNextRandomRecordWith(firstName, secondName);
        api.put(record);
        for (char literal: record.literals()) {
            softAssert.assertThat(api.get(literal).contains(record)).isTrue();
        }
        api.delete(record);

        T newRecord = getNextRandomRecordWith(firstName, secondName);
        api.put(newRecord);
        for (char literal: newRecord.literals()) {
            softAssert.assertThat(api.get(literal).contains(record)).isFalse();
            softAssert.assertThat(api.get(literal).contains(newRecord)).isFalse();
        }

        softAssert.assertAll();
    }

    // Adds some data twice.
    @Test
    public void twicePutAndGet() {
        SoftAssertions softAssert = new SoftAssertions();

        T record = getNextRandomRecord();
        api.put(record);
        api.put(record);
        for (char literal: record.literals()) {
            softAssert.assertThat(api.get(literal)).hasSize(1);
        }

        softAssert.assertAll();
    }
}