package ru.csc.bdse.app;

import ru.csc.bdse.ApplicationProperties;
import ru.csc.bdse.app.v2.service.BookRecordV2;
import ru.csc.bdse.app.v2.service.ServiceV2;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Random;

import java.util.Arrays;

public class PhoneBookServiceV2PhoneBookFunctionalTest extends AbstractPhoneBookFunctionalTest<BookRecordV2> {
    @Override
    protected PhoneBookApi<BookRecordV2> newPhoneBookApi() {
        ApplicationProperties properties = new ApplicationProperties();
        properties.setDbfile("test.db");
        return new ServiceV2(new BerkleyKeyValueApi(new BerkleyDataSource(properties)));
    }

    @Override
    protected BookRecordV2 getNextRandomRecord() {
        return new BookRecordV2(Random.nextKey(), Random.nextKey(), Random.nextKey(), Arrays.asList(Random.nextKey(), Random.nextKey()));
    }

    @Override
    protected BookRecordV2 getNextRandomRecordWith(String firstName, String secondName) {
        return new BookRecordV2(firstName, secondName, Random.nextKey(), Arrays.asList(Random.nextKey(), Random.nextKey()));
    }
}
