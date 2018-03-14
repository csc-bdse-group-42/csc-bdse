package ru.csc.bdse.app;

import ru.csc.bdse.ApplicationProperties;
import ru.csc.bdse.app.v1.service.ServiceV1;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.kv.BerkleyKeyValueApi;

public class ServiceV1PhoneBookFunctionalTest extends AbstractPhoneBookFunctionalTest {
    @Override
    protected PhoneBookApi newPhoneBookApi() {
        ApplicationProperties properties = new ApplicationProperties();
        properties.setDbfile("test.db");
        return new ServiceV1(new BerkleyKeyValueApi(new BerkleyDataSource(properties)));
    }
}
