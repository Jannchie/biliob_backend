package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Calendar;
import java.util.Date;

/**
 * @author jannchie
 */
@Document(collection = "ip_visit_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IpVisitRecord {
    @Id
    private ObjectId id;

    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+0")
    private Date datetime;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public IpVisitRecord(String ip) {
        this.ip = ip;
        this.datetime = Calendar.getInstance().getTime();
    }
}
