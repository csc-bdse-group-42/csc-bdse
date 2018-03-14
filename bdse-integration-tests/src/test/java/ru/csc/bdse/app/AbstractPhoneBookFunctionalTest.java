package ru.csc.bdse.app;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import ru.csc.bdse.util.Random;

public abstract class AbstractPhoneBookFunctionalTest {

    protected abstract PhoneBookApi newPhoneBookApi();
    private PhoneBookApi api = newPhoneBookApi();

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
    }

    @Test
    public void erasure() {
        //TODO cancel some records
    }

    @Test
    public void update() {
        //TODO update data and put some data twice
    }
}