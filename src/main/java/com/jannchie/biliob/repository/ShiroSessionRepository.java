package com.jannchie.biliob.repository;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Jannchie
 */
@Component
public class ShiroSessionRepository extends AbstractSessionDAO {

    private MongoTemplate mongoTemplate;

    @Autowired
    public ShiroSessionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    private void saveSession(Session session) {
        mongoTemplate.save(session, "sessions");
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return this.getSession(sessionId);
    }

    private Session getSession(Serializable sessionId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(sessionId)), Session.class, "sessions");
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        mongoTemplate.remove(session, "sessions");
    }


    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }
}
