package ru.csc.bdse.app.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.Record;
import ru.csc.bdse.app.v1.service.BookRecordV1;
import ru.csc.bdse.app.v2.service.BookRecordV2;

@RestController
public class BookControllerV2 {
    private final PhoneBookApi phoneBookApi;

    @Autowired
    public BookControllerV2(final PhoneBookApi phoneBookApi) {
        this.phoneBookApi = phoneBookApi;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/add-person-v2")
    public void putV2(@RequestParam("firstName") String firstName,
                      @RequestParam("secondName") String secondName,
                      @RequestParam("secondName") String nickName,
                      @RequestParam("phone1") String phone1,
                      @RequestParam("phone2") String phone2) {

        Record record = new BookRecordV2(firstName, secondName, nickName, phone1, phone2);
        phoneBookApi.put(record);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete-person-v2")
    public void deleteV2(@RequestParam("firstName") String firstName,
                         @RequestParam("secondName") String secondName) {
        Record record = new BookRecordV1(firstName, secondName);
        phoneBookApi.delete(record);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-person-v2")
    public void getV2(@RequestParam("firstName") char literal) {
        phoneBookApi.get(literal);
    }

}
