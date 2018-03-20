package ru.csc.bdse.app;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import ru.csc.bdse.ApplicationProperties;
import ru.csc.bdse.app.v10.service.BookRecordV1;
import ru.csc.bdse.app.v10.service.ServiceV1;
import ru.csc.bdse.app.v11.service.BookRecordV2;
import ru.csc.bdse.app.v11.service.ServiceV2;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.kv.BerkleyKeyValueApi;

import java.util.List;
import java.util.Set;

/**
 * Test have to be implemented
 *
 * @author alesavin
 */
public class PhoneBookCompatibilitiesTest {

    private ServiceV1 serviceV1;
    private ServiceV2 serviceV2;

    @Before
    public void init() {
        ApplicationProperties properties = new ApplicationProperties();
        properties.setDbfile("test.db");
        serviceV1 = new ServiceV1(new BerkleyKeyValueApi(new BerkleyDataSource(properties)));
        serviceV2 = new ServiceV2(new BerkleyKeyValueApi(new BerkleyDataSource(properties)));
    }

    @Test
    public void write10read11() {
        SoftAssertions softAssert = new SoftAssertions();

        BookRecordV1 recordV1 = new BookRecordV1("Vasya", "Pupkin", "89115467734");
        serviceV1.put(recordV1);

        Set<BookRecordV2> bookRecordV2Set = serviceV2.get('P');
        softAssert.assertThat(!bookRecordV2Set.isEmpty());
    }

    @Test
    public void write11read10() {
        SoftAssertions softAssert = new SoftAssertions();
        List<String> phones = Lists.newArrayList("89115467735", "89115467738");
        BookRecordV2 recordV2 = new BookRecordV2("Vasya", "Pupkin", "v_pupkin", phones);
        serviceV2.put(recordV2);

        Set<BookRecordV1> bookRecordV1Set = serviceV1.get('P');
        softAssert.assertThat(!bookRecordV1Set.isEmpty());
    }

    @Test
    public void write10erasure11() {
        SoftAssertions softAssert = new SoftAssertions();
        BookRecordV1 recordV1 = new BookRecordV1("Vasya", "Pupkin", "89115467734");
        serviceV1.put(recordV1);

        List<String> phones = Lists.newArrayList("89115467735", "89115467738");
        BookRecordV2 recordV2 = new BookRecordV2("Vasya", "Pupkin", "v_pupkin", phones);
        serviceV2.delete(recordV2);

        Set<BookRecordV1> bookRecordV1Set = serviceV1.get('P');
        softAssert.assertThat(bookRecordV1Set.isEmpty());
    }
}