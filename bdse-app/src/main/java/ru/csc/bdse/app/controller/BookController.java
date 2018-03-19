package ru.csc.bdse.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.csc.bdse.app.PhoneBookApi;
import ru.csc.bdse.app.Record;
import ru.csc.bdse.app.model.BookRecord;

import javax.websocket.server.PathParam;
import java.util.Set;

@RestController
public class BookController {
    private final PhoneBookApi phoneBookApi;

    public BookController(final PhoneBookApi phoneBookApi) {
        this.phoneBookApi = phoneBookApi;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/person")
    public String put(@RequestBody BookRecord record) {
        phoneBookApi.put(record);
        return "OK";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/person")
    public String deleteV1(@RequestParam("firstName") String firstName,
                         @RequestParam("secondName") String secondName) {
        Record record = new BookRecord(firstName, secondName);
        phoneBookApi.delete(record);
        return "OK";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/person/{literal}")
    public Set<BookRecord> get(@PathVariable("literal") char literal) {
        return phoneBookApi.get(literal);
    }
}
