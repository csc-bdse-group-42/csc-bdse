package ru.csc.bdse.app;

import ru.csc.bdse.ApplicationProperties;
import ru.csc.bdse.app.v1.service.BookRecordV1;
import ru.csc.bdse.app.v1.service.ServiceV1;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Random;

public class ServiceV1PhoneBookFunctionalTest extends AbstractPhoneBookFunctionalTest<BookRecordV1> {
    @Override
    protected PhoneBookApi<BookRecordV1> newPhoneBookApi() {
        ApplicationProperties properties = new ApplicationProperties();
        properties.setDbfile("test.db");
        return new ServiceV1(new BerkleyKeyValueApi(new BerkleyDataSource(properties)));
    }

    @Override
    protected BookRecordV1 getNextRandomRecord() {
        return new BookRecordV1(Random.nextKey(), Random.nextKey(), Random.nextKey());
    }
}
