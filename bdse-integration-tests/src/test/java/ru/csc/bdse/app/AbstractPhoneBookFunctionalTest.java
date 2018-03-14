package ru.csc.bdse.app;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import ru.csc.bdse.util.Random;

public abstract class AbstractPhoneBookFunctionalTest<T extends Record> {

    protected abstract PhoneBookApi<T> newPhoneBookApi();
    private PhoneBookApi<T> api = newPhoneBookApi();

    protected abstract T getNextRandomRecord();

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

    @Test
    public void update() {
        //TODO update data and put some data twice
    }
}