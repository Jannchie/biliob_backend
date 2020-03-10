package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "index_visit")
public class JannchieIndexVisitRecord {
    private String keyword;
    private Date visitTime;
}
