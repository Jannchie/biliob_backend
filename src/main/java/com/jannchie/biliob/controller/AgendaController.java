package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.*;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.model.Agenda;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

/**
 * @author Jannchie
 */
@RestController
public class AgendaController {
    MongoTemplate mongoTemplate;
    CreditOperateHandle creditOperateHandle;
    private Logger logger = LogManager.getLogger();

    @Autowired
    public AgendaController(MongoTemplate mongoTemplate, CreditOperateHandle creditOperateHandle) {
        this.mongoTemplate = mongoTemplate;
        this.creditOperateHandle = creditOperateHandle;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/agenda")
    public List<Agenda> listAgenda(@RequestParam(value = "sort", defaultValue = "1") Integer sort, @RequestParam(value = "filter", defaultValue = "1") Integer filter, @RequestParam("p") Integer page) {
        Criteria c;
        if (filter == 1) {
            c = Criteria.where("state").in(AgendaState.WAITING, AgendaState.PENDING);
        } else {
            c = Criteria.where("state").in(AgendaState.CLOSED, AgendaState.DUPLICATE, AgendaState.FINISHED);
        }

        switch (sort) {
            case 1:
                return mongoTemplate.find(new Query(c).with(PageRequest.of(page, PageSizeEnum.BIG_SIZE.getValue(), Sort.by("score").descending())), Agenda.class);
            case 2:
                return mongoTemplate.find(new Query(c).with(PageRequest.of(page, PageSizeEnum.BIG_SIZE.getValue(), Sort.by("createTime").descending())), Agenda.class);
            default:
                break;
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda")
    public Result<?> postAgenda(@RequestBody @Validated Agenda agenda) {
        // 判断用户身份
        User user = UserUtils.getFullInfo();
        Integer level = UserUtils.getUserRoleLevel(user);
        if (level < RoleEnum.OBSERVER.getLevel()) {
            return new Result<>(ResultEnum.PERMISSION_DENIED);
        }
        agenda.setAgainstCount(0);
        agenda.setAgainstScore(0);
        agenda.setCreateTime(Calendar.getInstance().getTime());
        agenda.setFavorCount(0);
        agenda.setFavorScore(0);
        agenda.setState(AgendaState.WAITING);
        agenda.setFinishTime(null);
        agenda.setScore(0);
        agenda.setVotes(null);
        agenda.setCreator(new User(user.getId()));
        // 装载Agenda
        creditOperateHandle.doCreditOperate(CreditConstant.POST_AGENDA, agenda.getTitle(), () -> mongoTemplate.save(agenda));
        return new Result<>(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/agenda")
    public Result<?> deleteAgenda(@RequestBody @Validated String id) {
        ObjectId userId = UserUtils.getUserId();
        Agenda a = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), Agenda.class);
        if (a != null && a.getCreator().getId().equals(userId)) {
            mongoTemplate.remove(a);
            logger.info("用户 {} 删除议题 {}", userId, a.getTitle());
            return ResultEnum.SUCCEED.getResult();
        }
        return ResultEnum.EXECUTE_FAILURE.getResult();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/support")
    public Result<?> supportAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/against")
    public Result<?> againstAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/abstain")
    public Result<?> abstainAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/done")
    public Result<?> finishAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/doing")
    public Result<?> startAgenda() {
        return null;
    }

}
