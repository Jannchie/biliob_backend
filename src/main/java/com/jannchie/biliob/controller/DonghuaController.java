package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.DonghuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get the Bangumi Data.
 *
 * @author jannchie
 */
@RestController
public class DonghuaController {

    private final DonghuaService donghuaService;

    @Autowired
    public DonghuaController(DonghuaService donghuaService) {
        this.donghuaService = donghuaService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/donghua")
    public ResponseEntity<?> listOnline(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        return donghuaService.listDonghua(page, pagesize);
    }
}
