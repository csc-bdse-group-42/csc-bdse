package ru.csc.bdse.app.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.Record;
import ru.csc.bdse.app.v1.service.BookRecordV1;

@RestController
public class BookControllerV1 {
    private final PhoneBookApi phoneBookApi;

    @Autowired
    public BookControllerV1(final PhoneBookApi phoneBookApi) {
        this.phoneBookApi = phoneBookApi;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/add-person-v1")
    public void putV1(@RequestParam("firstName") String firstName,
                      @RequestParam("secondName") String secondName,
                      @RequestParam("phone") String phone) {

        Record record = new BookRecordV1(firstName, secondName, phone);
        phoneBookApi.put(record);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete-person-v1")
    public void deleteV1(@RequestParam("firstName") String firstName,
                         @RequestParam("secondName") String secondName) {
        Record record = new BookRecordV1(firstName, secondName);
        phoneBookApi.delete(record);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-person-v1")
    public void getV1(@RequestParam("firstName") char literal) {
        phoneBookApi.get(literal);
    }
}
