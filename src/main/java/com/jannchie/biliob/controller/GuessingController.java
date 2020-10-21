package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.FansGuessingItem;
import com.jannchie.biliob.service.GuessingService;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jannchie
 */
@RestController
public class GuessingController {
    @Autowired
    GuessingService guessingService;

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/fans-guessing")
    public List<FansGuessingItem> listFansGuessing(
            @RequestParam(name = "p", defaultValue = "0") Integer page) {
        return guessingService.listFansGuessing(page);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/author/fans-guessing/{guessingId}")
    public Result<?> joinGuessing(@RequestBody FansGuessingItem.PokerChip pokerChip, @PathVariable String guessingId) {
        return guessingService.joinFansGuessing(guessingId, pokerChip);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/fans-guessing/{guessingId}/result")
    public Result<?> getGuessingResult(@PathVariable String guessingId) {
        return guessingService.getGuessingResult(guessingId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/author/fans-guessing/{guessingId}/cancel")
    public Result<?> cancelRevenue(@PathVariable String guessingId) {
        return guessingService.cancelRevenue(guessingId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/fans-guessing/judge")
    public void judge() {
        guessingService.judgeFinishedFansGuessing();
    }
}
