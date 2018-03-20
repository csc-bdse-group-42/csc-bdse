package ru.csc.bdse.app.v11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.Record;
import ru.csc.bdse.app.v10.model.BookRecordV10;
import ru.csc.bdse.app.v11.model.BookRecordV11;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1.1/person")
public class BookControllerV11 {
    private final PhoneBookApi phoneBookApi;

    @Autowired
    public BookControllerV11(final PhoneBookApi phoneBookApi) {
        this.phoneBookApi = phoneBookApi;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String put(@RequestBody BookRecordV11 record) {
        phoneBookApi.put(record);
        return "OK";
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(@RequestParam("firstName") String firstName,
                         @RequestParam("secondName") String secondName) {
        Record record = new BookRecordV11(firstName, secondName);
        phoneBookApi.delete(record);
        return "OK";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{literal}")
    public Set<BookRecordV11> get(@PathVariable("literal") char literal) {
        return phoneBookApi.get(literal);
    }
}
