package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.BankService;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jannchie
 */
@RestController
public class BankController {
    BankService bankService;

    @Autowired
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/api/user/bank")
    public Result<?> getMyCredit() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/bank/save")
    public Result<?> saveCredit(Integer credit, Integer type) {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/bank/withdrawal")
    public Result<?> withdrawalCredit() {
        return null;
    }
}
