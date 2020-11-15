package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jannchie
 */
@Document("video_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoInfo {
    private Long aid;
    // private Integer attribute;
    private String bvid;
    private Long cid;
    private Integer copyright;
    private Long ctime;
    private String desc;
    private Long duration;
    private String dynamic;
    private Author owner;
    private String pic;
    private Long pubdate;
    private HashMap<String, Short> rights;
    private VideoStat stat;
    private Short tid;
    private String title;
    private String tname;
    private Short videos;
    private List<String> tag;

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

//    public Integer getAttribute() {
//        return attribute;
//    }
//
//    public void setAttribute(Integer attribute) {
//        this.attribute = attribute;
//    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Integer getCopyright() {
        return copyright;
    }

    public void setCopyright(Integer copyright) {
        this.copyright = copyright;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public Author getOwner() {
        return owner;
    }

    public void setOwner(Author owner) {
        this.owner = owner;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Long getPubdate() {
        return pubdate;
    }

    public void setPubdate(Long pubdate) {
        this.pubdate = pubdate;
    }

    public HashMap<String, Short> getRights() {
        return rights;
    }

    public void setRights(HashMap<String, Short> rights) {
        this.rights = rights;
    }

    public VideoStat getStat() {
        return stat;
    }

    public void setStat(VideoStat stat) {
        this.stat = stat;
    }

    public Short getTid() {
        return tid;
    }

    public void setTid(Short tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public Short getVideos() {
        return videos;
    }

    public void setVideos(Short videos) {
        this.videos = videos;
    }
}
