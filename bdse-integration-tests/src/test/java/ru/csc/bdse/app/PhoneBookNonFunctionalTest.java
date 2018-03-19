package ru.csc.bdse.app;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import ru.csc.bdse.ApplicationProperties;
import ru.csc.bdse.app.model.BookRecord;
import ru.csc.bdse.app.service.PhoneBookService;
import ru.csc.bdse.datasource.BerkleyDataSource;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.kv.NodeAction;
import ru.csc.bdse.util.IllegalNodeStateException;

import java.util.Set;

/**
 * Test have to be implemented
 *
 * @author alesavin
 */
public class PhoneBookNonFunctionalTest {

    private PhoneBookService phoneBookService;
    private BerkleyKeyValueApi berkleyKeyValueApi;

    @Before
    public void init() {
        ApplicationProperties properties = new ApplicationProperties();
        properties.setDbfile("test.db");

        berkleyKeyValueApi = new BerkleyKeyValueApi(new BerkleyDataSource(properties));
        berkleyKeyValueApi.setNodeName("testNode");
        berkleyKeyValueApi.action("testNode", NodeAction.DOWN);

        phoneBookService = new PhoneBookService(berkleyKeyValueApi);
    }

//    @Test(expected = IllegalNodeStateException.class)
//    public void putGetErasureWithStoppedNode() {
//        SoftAssertions softAssert = new SoftAssertions();
//
//        BookRecord recordV1 = new BookRecord("Vasya", "Pupkin", "89115467734");
//        phoneBookService.put(recordV1);
//
//        Set<BookRecord> bookRecordSet = phoneBookService.get('P');
//        softAssert.assertThat(!bookRecordSet.isEmpty());
//    }
//
//    @Test
//    public void dataWasSavedIfAppRestarts() {
//        // TODO test data was saved after ru.csc.bdse.app restarts
//    }
//
//    @Test
//    public void dataWasSavedIfKvNodeRestarts() {
//        SoftAssertions softAssert = new SoftAssertions();
//        berkleyKeyValueApi.action("testNode", NodeAction.UP);
//
//        BookRecord recordV1 = new BookRecord("Vasya", "Pupkin", "89115467734");
//        phoneBookService.put(recordV1);
//
//        ApplicationProperties properties = new ApplicationProperties();
//        properties.setDbfile("test.db");
//        berkleyKeyValueApi = new BerkleyKeyValueApi(new BerkleyDataSource(properties));
//
//        phoneBookService = new PhoneBookService(berkleyKeyValueApi);
//
//        Set<BookRecord> bookRecordSet = phoneBookService.get('P');
//        softAssert.assertThat(!bookRecordSet.isEmpty());
//    }
}