package com.jannchie.biliob.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * @author jannchie
 */
public class Author {
    @Id
    private ObjectId id;
    private Long mid;
    private String name;
    private String face;
    private String sex;
    private String official;
    private Integer level;
    private ArrayList<Data> data;
    private ArrayList<Channel> channel;

    public Author(Long mid) {
        this.mid = mid;

    }

    public ArrayList<Channel> getChannel() {
        return channel;
    }

    public void setChannel(ArrayList<Channel> channel) {
        this.channel = channel;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public class Data {
        private Integer fans;
        private Integer attention;
        private Integer archive;
        private Integer article;
        private Date datetime;

        public Integer getFans() {
            return fans;
        }

        public Integer getAttention() {
            return attention;
        }

        public Integer getArchive() {
            return archive;
        }

        public Integer getArticle() {
            return article;
        }

        public Date getDatetime() {
            return datetime;
        }
    }

    public class Channel {
        private Integer tid;
        private Integer count;
        private Integer name;

        public Integer getTid() {
            return tid;
        }

        public void setTid(Integer tid) {
            this.tid = tid;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getName() {
            return name;
        }

        public void setName(Integer name) {
            this.name = name;
        }
    }
}
