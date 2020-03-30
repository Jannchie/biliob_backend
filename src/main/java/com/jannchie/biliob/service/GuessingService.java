package com.jannchie.biliob.service;

import com.jannchie.biliob.model.GuessingItem;
import com.jannchie.biliob.utils.Result;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jannchie
 */
@Service
public class GuessingService {
    public List<GuessingItem> listLatestGuessing(Integer page) {
        return null;
    }

    public Result<?> makeBet(ObjectId guessingId, Integer index, Double value) {
        return null;
    }

    public Result<?> announceResult(ObjectId guessingId, Integer index) {
        return null;
    }
}
