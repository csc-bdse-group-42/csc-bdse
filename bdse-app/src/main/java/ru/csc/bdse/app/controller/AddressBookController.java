package ru.csc.bdse.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.Record;
import ru.csc.bdse.app.service.BookRecordV1;
import ru.csc.bdse.app.service.BookRecordV2;

@RestController
public class AddressBookController {
    private final PhoneBookApi phoneBookApi;

    @Autowired
    public AddressBookController(final PhoneBookApi phoneBookApi) {
        this.phoneBookApi = phoneBookApi;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/key-value1/{name}")
    public void putV1(@PathVariable final String name,
                      @RequestParam("phone") String phone) {

        Record record = new BookRecordV1(name, phone);
        phoneBookApi.put(record);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/key-value2/{name}")
    public void putV2(@PathVariable final String name,
                      @RequestParam("phone1") String phone1,
                      @RequestParam("phone2") String phone2) {

        Record record = new BookRecordV2(name, phone1, phone2);
        phoneBookApi.put(record);
    }
}
