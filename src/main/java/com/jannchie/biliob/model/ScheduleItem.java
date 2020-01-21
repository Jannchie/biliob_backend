package com.jannchie.biliob.model;

import java.util.ArrayList;

/**
 * @author jannchie
 */
public class ScheduleItem {
    private String name;
    private ArrayList<String> idList;
    private String type;
    private Integer frequency;
    private String owner;

    public ScheduleItem() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<String> idList) {
        this.idList = idList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}
