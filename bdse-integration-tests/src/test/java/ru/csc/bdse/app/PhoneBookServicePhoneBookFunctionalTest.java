package ru.csc.bdse.app;

import ru.csc.bdse.ApplicationProperties;
import ru.csc.bdse.app.model.BookRecord;
import ru.csc.bdse.app.service.PhoneBookService;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.util.Random;

public class PhoneBookServicePhoneBookFunctionalTest extends AbstractPhoneBookFunctionalTest<BookRecord> {
    @Override
    protected PhoneBookApi<BookRecord> newPhoneBookApi() {
        ApplicationProperties properties = new ApplicationProperties();
        properties.setDbfile("test.db");
        return new PhoneBookService(new BerkleyKeyValueApi(new BerkleyDataSource(properties)));
    }

    @Override
    protected BookRecord getNextRandomRecord() {
        return new BookRecord(Random.nextKey(), Random.nextKey(), Random.nextKey());
    }

    @Override
    protected BookRecord getNextRandomRecordWith(String firstName, String secondName) {
        return new BookRecord(firstName, secondName, Random.nextKey());
    }
}
