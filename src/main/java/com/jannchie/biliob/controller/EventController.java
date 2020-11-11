package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jannchie
 */
@RestController
public class EventController {
    @Autowired
    private EventService eventService;

    @RequestMapping(method = RequestMethod.GET, value = "/api/event")
    public ResponseEntity<?> pageEvent(@RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "20") Integer pagesize) {
        return new ResponseEntity<>(eventService.pageEvent(page, pagesize), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/event/fans-variation")
    public ResponseEntity<?> listFansVariation(@RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "20") Integer pagesize) {
        return new ResponseEntity<>(eventService.listFansVariation(page, pagesize), HttpStatus.OK);
    }
}
