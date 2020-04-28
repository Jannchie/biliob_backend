package com.jannchie.biliob.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Jannchie
 */
@Document("object_visit_record")
public class ObjectVisitRecord {
    private String type;
    private ObjectId userId;
    private ObjectId objectId;
    private Date visitTime;

    public ObjectVisitRecord(String type, ObjectId userId, ObjectId objectId, Date visitTime) {
        this.type = type;
        this.userId = userId;
        this.objectId = objectId;
        this.visitTime = visitTime;
    }

    public String getType() {
        return type;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public Date getVisitTime() {
        return visitTime;
    }
}
