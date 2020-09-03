package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.*;
import com.jannchie.biliob.model.Agenda;
import com.jannchie.biliob.model.AgendaVote;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AgendaControllerTest {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    AgendaController agendaController;

    @Autowired
    MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        ObjectId uid = UserUtils.getUserId();
        mongoTemplate.remove(Query.query(Criteria.where(DbFields.CREATOR_ID).is(uid)), Agenda.class);
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void listAgenda() {
        agendaController.listAgenda(0, 0, 0);
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void postAgendaAndDeleteAgenda() {
        String agendaId = postAgendaAndGetAgendaId();
        removeAgenda(agendaId);
    }


    @Test
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void postAgendaWithOutPermit() {
        logger.info("Test post agenda but has not permitted.");
        Result<?> r = agendaController.postAgenda(getAgenda());
        Assert.assertSame(r.getMsg(), r.getCode(), -1);
    }

    private Agenda getAgenda() {
        Agenda a = new Agenda();
        a.setDesc("Test");
        a.setTitle("Test");
        a.setType(AgendaTypeEnum.ENHANCE.getValue());
        return a;
    }


    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void supportAgenda() {
        vote(UserOpinion.IN_FAVOR);
    }

    private void vote(UserOpinion opinion) {

        ObjectId uid = UserUtils.getUserId();
        String agendaId = postAgendaAndGetAgendaId();
        switch (opinion) {
            case AGAINST:
                agendaController.againstAgenda(agendaId);
                break;
            case IN_FAVOR:
                agendaController.supportAgenda(agendaId);
                break;
            default:
                agendaController.abstentionAgenda(agendaId);
                break;
        }
        AgendaVote av = mongoTemplate.findOne(Query.query(Criteria.where(DbFields.USER_ID).is(uid).and(DbFields.AGENDA_ID).is(agendaId)), AgendaVote.class);
        Assert.assertNotNull(av);
        Agenda a = mongoTemplate.findOne(Query.query(Criteria.where(DbFields.ID).is(agendaId)), Agenda.class);
        Assert.assertNotNull(a);
        Double fs = a.getFavorScore();
        Integer fc = a.getFavorCount();
        Integer ac = a.getAgainstCount();
        Double as = a.getAgainstScore();
        User u = UserUtils.getFullInfo();
        switch (opinion) {
            case AGAINST:
                Assert.assertEquals(u.getExp(), as);
                Assert.assertEquals(1, ac.longValue());
                Assert.assertEquals(0, fc.longValue());
                Assert.assertEquals(0, fs.longValue());
                break;
            case IN_FAVOR:
                Assert.assertEquals(u.getExp(), fs);
                Assert.assertEquals(1, fc.longValue());
                Assert.assertEquals(0, ac.longValue());
                Assert.assertEquals(0, as.longValue());
                break;
            default:
                Assert.assertEquals(0, fs.longValue());
                Assert.assertEquals(0, fc.longValue());
                Assert.assertEquals(0, ac.longValue());
                Assert.assertEquals(0, as.longValue());
                break;
        }
        removeAgenda(agendaId);
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void againstAgenda() {
        vote(UserOpinion.AGAINST);
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void abstainAgenda() {
        vote(UserOpinion.ABSTENTION);
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void finishAgenda() {
        String id = postAgendaAndGetAgendaId();
        agendaController.finishAgenda(id);
        Agenda a = agendaController.getAgenda(id);
        Assert.assertEquals(a.getState().longValue(), AgendaState.FINISHED.getValue().longValue());
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void startAgenda() {
        String id = postAgendaAndGetAgendaId();
        agendaController.startAgenda(id);
        Agenda a = agendaController.getAgenda(id);
        Assert.assertEquals(a.getState().longValue(), AgendaState.PENDING.getValue().longValue());
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void getCount() {
        agendaController.getStateCatalogCount();
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void deleteAgenda() {
        logger.info("Test delete agenda");
        User user = mongoTemplate.findOne(Query.query(Criteria.where(DbFields.NAME).is(TestConstants.TEST_USER_NAME)), User.class);
        assert user != null;
        List<Agenda> agendas = mongoTemplate.find(Query.query(Criteria.where(DbFields.CREATOR_ID).is(user.getId())), Agenda.class);
        logger.info("Test's agendas count: {}", agendas.size());
        for (Agenda a : agendas) {
            String id = a.getId();
            agendaController.deleteAgenda(id);
            a = mongoTemplate.findOne(Query.query(Criteria.where(DbFields.CREATOR_ID).is(user.getId())), Agenda.class);
            Assert.assertNull(a);
        }
    }

    private void removeAgenda(String agendaId) {
        agendaController.deleteAgenda(agendaId);
        long count = mongoTemplate.count(Query.query(Criteria.where(DbFields.ID).is(agendaId)), Agenda.class);
        Assert.assertSame(0L, count);
    }

    private String postAgendaAndGetAgendaId() {
        agendaController.postAgenda(getAgenda());
        ObjectId uid = UserUtils.getUserId();
        Assert.assertNotNull(uid);
        List<Agenda> agendaList = mongoTemplate.find(Query.query(Criteria.where(DbFields.CREATOR_ID).is(uid)), Agenda.class);
        Assert.assertSame(1, agendaList.size());
        return agendaList.get(0).getId();
    }

    @Test
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void updateAgendaState() {
        Agenda a = getAgenda();
        mongoTemplate.save(a);
        agendaController.updateAgendaState(a.getId(), AgendaState.PENDING.getValue());
        Agenda s = mongoTemplate.findOne(Query.query(Criteria.where(DbFields.ID).is(a.getId())), Agenda.class);
        assert s != null;
        assert s.getState().equals(AgendaState.PENDING.getValue());
    }

    @After
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void after() {
        ObjectId userId = UserUtils.getUserId();
        mongoTemplate.remove(Query.query(Criteria.where(DbFields.CREATOR_ID).is(userId)), Agenda.class);
        mongoTemplate.remove(Query.query(Criteria.where(DbFields.USER_ID).is(userId)), AgendaVote.class);
    }

}