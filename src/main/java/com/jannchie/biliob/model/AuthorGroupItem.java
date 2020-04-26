package com.jannchie.biliob.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Jannchie
 */
@Document("author_group_item")
public class AuthorGroupItem {
    private Long mid;
    private ObjectId gid;

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public ObjectId getGid() {
        return gid;
    }

    public void setGid(ObjectId gid) {
        this.gid = gid;
    }
}