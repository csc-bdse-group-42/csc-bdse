package ru.csc.bdse.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.Record;
import ru.csc.bdse.app.model.BookRecord;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/person")
public class BookController {
    private final PhoneBookApi phoneBookApi;

    @Autowired
    public BookController(final PhoneBookApi phoneBookApi) {
        this.phoneBookApi = phoneBookApi;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String put(@RequestBody BookRecord record) {
        phoneBookApi.put(record);
        return "OK";
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(@RequestParam("firstName") String firstName,
                         @RequestParam("secondName") String secondName) {
        Record record = new BookRecord(firstName, secondName);
        phoneBookApi.delete(record);
        return "OK";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{literal}")
    public Set<BookRecord> get(@PathVariable("literal") char literal) {
        return phoneBookApi.get(literal);
    }

}
