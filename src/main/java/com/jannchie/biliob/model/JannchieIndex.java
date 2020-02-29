package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jannchie.biliob.object.JannchieIndexData;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class JannchieIndex {
    private String name;
    private List<JannchieIndexData> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JannchieIndexData> getData() {
        return data;
    }

    public void setData(List<JannchieIndexData> data) {
        this.data = data;
    }
}
