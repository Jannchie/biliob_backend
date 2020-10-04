package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.*;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.form.AgendaCatalogCountResult;
import com.jannchie.biliob.model.Agenda;
import com.jannchie.biliob.model.AgendaStateCount;
import com.jannchie.biliob.model.AgendaVote;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import com.mongodb.client.result.UpdateResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
        if (filter == 0) {
            c = Criteria.where("state").in(AgendaState.WAITING.getValue());
        } else if (filter == 1) {
            c = Criteria.where("state").in(AgendaState.PENDING.getValue());
        } else if (filter == 2) {
            c = Criteria.where("state").in(AgendaState.FINISHED.getValue());
        } else {
            c = Criteria.where("state").in(AgendaState.CLOSED.getValue(), AgendaState.DUPLICATE.getValue());
        }

        switch (sort) {
            case 1:
                return mongoTemplate.aggregate(Aggregation.newAggregation(
                        Aggregation.match(c),
                        Aggregation.sort(Sort.by("score").descending()),
                        Aggregation.skip(page * PageSizeEnum.BIG_SIZE.getValue()),
                        Aggregation.lookup(DbFields.USER, DbFields.CREATOR_ID, DbFields.ID, DbFields.CREATOR),
                        Aggregation.project().andExpression("{password: 0, ip: 0,  favoriteMid: 0, favoriteAid: 0, mail: 0, credit: 0}").as(DbFields.CREATOR),
                        Aggregation.limit(PageSizeEnum.BIG_SIZE.getValue())
                ), Agenda.class, Agenda.class).getMappedResults();
            case 2:
                return mongoTemplate.aggregate(Aggregation.newAggregation(
                        Aggregation.match(c),
                        Aggregation.sort(Sort.by("createTime").descending()),
                        Aggregation.skip(page * PageSizeEnum.BIG_SIZE.getValue()),
                        Aggregation.lookup(DbFields.USER, DbFields.CREATOR_ID, DbFields.ID, DbFields.CREATOR),
                        Aggregation.project().andExpression("{password: 0, ip: 0,  favoriteMid: 0, favoriteAid: 0, mail: 0, credit: 0 }").as(DbFields.CREATOR),
                        Aggregation.limit(PageSizeEnum.BIG_SIZE.getValue())
                ), Agenda.class, Agenda.class).getMappedResults();
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
        if (level < RoleEnum.LEVEL_2.getLevel()) {
            return new Result<>(ResultEnum.PERMISSION_DENIED);
        }
        agenda.setAgainstCount(0);
        agenda.setAgainstScore(0D);
        agenda.setCreateTime(Calendar.getInstance().getTime());
        agenda.setFavorCount(0);
        agenda.setFavorScore(0D);
        agenda.setState(AgendaState.WAITING.getValue());
        agenda.setFinishTime(null);
        agenda.setScore(0);
        agenda.setVotes(null);
        agenda.setCreator(new User(user.getId()));
        // 装载Agenda

        return creditOperateHandle.doCreditOperate(user, CreditConstant.POST_AGENDA, agenda.getTitle(), () -> mongoTemplate.save(agenda));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/agenda/{id}")
    public Result<?> deleteAgenda(@PathVariable("id") String id) {
        ObjectId userId = UserUtils.getUserId();
        Agenda a = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), Agenda.class);
        if (a != null && a.getCreator().getId().equals(userId)) {
            mongoTemplate.remove(a);
            logger.info("用户 {} 删除议题 {}", userId, a.getTitle());
            return ResultEnum.SUCCEED.getResult();
        }
        return ResultEnum.EXECUTE_FAILURE.getResult();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/{id}/support")
    public Result<?> supportAgenda(@PathVariable("id") String id) {
        UserOpinion userOpinion = UserOpinion.IN_FAVOR;
        return getUserOperateResult(id, userOpinion);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/{id}/against")
    public Result<?> againstAgenda(@PathVariable("id") String id) {
        UserOpinion userOpinion = UserOpinion.AGAINST;
        return getUserOperateResult(id, userOpinion);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/{id}/abstain")
    public Result<?> abstentionAgenda(@PathVariable("id") String id) {
        UserOpinion userOpinion = UserOpinion.ABSTENTION;
        return getUserOperateResult(id, userOpinion);
    }

    private void updateAgendaData(String agendaId) {
        List<AgendaVote> agendaVoteList = mongoTemplate.find(Query.query(Criteria.where(DbFields.AGENDA_ID).is(agendaId)), AgendaVote.class);

        Double againstScore = 0D, favorScore = 0D;
        int againstCount = 0, favorCount = 0;

        for (AgendaVote agendaVote : agendaVoteList
        ) {
            User user = UserUtils.getUserById(agendaVote.getUser().getId());
            if (UserOpinion.IN_FAVOR.getValue().equals(agendaVote.getOpinion())) {
                favorCount += 1;
                favorScore += user.getExp();
            } else if (UserOpinion.AGAINST.getValue().equals(agendaVote.getOpinion())) {
                againstCount += 1;
                againstScore += user.getExp();
            }
        }
        mongoTemplate.updateFirst(Query.query(Criteria.where(DbFields.ID).is(agendaId)),
                Update.update(DbFields.AGAINST_COUNT, againstCount)
                        .set(DbFields.AGAINST_SCORE, againstScore)
                        .set(DbFields.FAVOR_SCORE, favorScore)
                        .set(DbFields.FAVOR_COUNT, favorCount)
                        .set(DbFields.SCORE, favorScore - againstScore)
                        .set(DbFields.UPDATE_TIME, Calendar.getInstance().getTime())
                , Agenda.class);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/{id}/done")
    public Result<?> finishAgenda(@PathVariable("id") String id) {
        UpdateResult ur = mongoTemplate.updateFirst(Query.query(Criteria.where(DbFields.ID).is(id)),
                Update.update(DbFields.STATE, AgendaState.FINISHED.getValue())
                        .set(DbFields.FINISH_TIME, Calendar.getInstance().getTime()), Agenda.class);
        return ResultEnum.SUCCEED.getResult(ur);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/{id}/doing")
    public Result<?> startAgenda(@PathVariable("id") String id) {
        UpdateResult ur = mongoTemplate.updateFirst(Query.query(Criteria.where(DbFields.ID).is(id)),
                Update.update(DbFields.STATE, AgendaState.PENDING.getValue()), Agenda.class);
        return ResultEnum.SUCCEED.getResult(ur);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/agenda/{id}")
    public Agenda getAgenda(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where(DbFields.ID).is(id)), Agenda.class);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/{id}/close")
    public Result<?> closeAgenda(@PathVariable("id") String id) {
        UpdateResult ur = mongoTemplate.updateFirst(Query.query(Criteria.where(DbFields.ID).is(id)),
                Update.update(DbFields.STATE, AgendaState.CLOSED.getValue()), Agenda.class);
        return ResultEnum.SUCCEED.getResult(ur);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/agenda/{id}/state/{state}")
    public Result<?> updateAgendaState(@PathVariable("id") String id, @PathVariable("state") Byte state) {
        if (UserUtils.getUserRoleLevel() < RoleEnum.LEVEL_5.getLevel()) {
            return ResultEnum.PERMISSION_DENIED.getResult();
        }
        UpdateResult ur = mongoTemplate.updateFirst(Query.query(Criteria.where(DbFields.ID).is(id)),
                Update.update(DbFields.STATE, state), Agenda.class);
        return ResultEnum.SUCCEED.getResult(ur);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/api/agenda/{id}/duplicate")
    public Result<?> duplicateAgenda(@PathVariable("id") String id) {
        UpdateResult ur = mongoTemplate.updateFirst(Query.query(Criteria.where(DbFields.ID).is(id)),
                Update.update(DbFields.STATE, AgendaState.DUPLICATE.getValue()), Agenda.class);
        return ResultEnum.SUCCEED.getResult(ur);
    }

    private Result<?> getUserOperateResult(@PathVariable("id") String id, UserOpinion userOpinion) {
        ObjectId uid = UserUtils.getUserId();
        if (uid == null) {
            return ResultEnum.HAS_NOT_LOGGED_IN.getResult();
        }
        mongoTemplate.upsert(Query.query(Criteria.where(DbFields.AGENDA_ID).is(id).and(DbFields.USER_ID).is(uid)),
                Update.update(DbFields.OPINION, userOpinion.getValue()).set(DbFields.USER_ID, uid),
                AgendaVote.class);
        updateAgendaData(id);
        return new Result<>(ResultEnum.SUCCEED, mongoTemplate.findOne(Query.query(Criteria.where(DbFields.ID).is(id)), Agenda.class));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/agenda/state")
    public AgendaStateCount getStateCatalogCount() {

        List<AgendaCatalogCountResult> data = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.group(DbFields.STATE).count().as(DbFields.COUNT),
                        Aggregation.project(DbFields.COUNT).and(DbFields.ID).as(DbFields.AGENDA_STATE)
                ), Agenda.class, AgendaCatalogCountResult.class
        ).getMappedResults();

        AgendaStateCount a = new AgendaStateCount();
        for (AgendaCatalogCountResult m : data) {
            byte state = m.getAgendaState();
            if (state == AgendaState.WAITING.getValue()) {
                a.setWaiting(m.getCount());
            } else if (state == AgendaState.PENDING.getValue()) {
                a.setPending(m.getCount());
            } else if (state == AgendaState.FINISHED.getValue()) {
                a.setFinished(m.getCount());
            } else if (state == AgendaState.CLOSED.getValue()) {
                a.setClosed(m.getCount());
            }
        }
        return a;
    }
}
