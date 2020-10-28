package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jannchie.biliob.constant.AuthorAchievementEnum;
import com.jannchie.biliob.constant.AuthorUniqueAchievementEnum;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {
    private List<Achievement> achievements;
    private Long mid;
    private String name;
    private String face;
    private String sex;
    private String official;
    private Integer level;
    private ArrayList<Data> data;
    private Object channels;
    private Rank rank;
    private Boolean focus;
    private Integer obInterval;
    private Boolean forceFocus;
    private Integer cRate;
    private ArrayList<String> keyword;
    private Integer cFans;
    @Field("cArchive_view")
    private Long cArchiveView;
    @Field("cArticle_view")
    private Long cArticleView;
    private Long cLike;
    private Author.Data cData;

    public Author() {
    }

    public Author(Long mid) {
        this.mid = mid;
        focus = true;
    }

    public Data getcData() {
        return cData;
    }

    public void setcData(Data cData) {
        this.cData = cData;
    }

    public Long getcLike() {
        return cLike;
    }

    public void setcLike(Long cLike) {
        this.cLike = cLike;
    }

    public Long getcArchiveView() {
        return cArchiveView;
    }

    public void setcArchiveView(Long cArchiveView) {
        this.cArchiveView = cArchiveView;
    }

    public Long getcArticleView() {
        return cArticleView;
    }

    public void setcArticleView(Long cArticleView) {
        this.cArticleView = cArticleView;
    }

    public Integer getcFans() {
        return cFans;
    }

    public void setcFans(Integer cFans) {
        this.cFans = cFans;
    }

    public ArrayList<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(ArrayList<String> keyword) {
        this.keyword = keyword;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Integer getcRate() {
        return cRate;
    }

    public void setcRate(Integer cRate) {
        this.cRate = cRate;
    }

    public Boolean getForceFocus() {
        return forceFocus;
    }

    public void setForceFocus(Boolean forceFocus) {
        this.forceFocus = forceFocus;
    }

    public Object getChannels() {
        return channels;
    }

    public void setChannels(Object channels) {
        this.channels = channels;
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

    public Boolean getFocus() {
        return focus;
    }

    public void setFocus(Boolean focus) {
        this.focus = focus;
    }


    public Integer getObInterval() {
        return obInterval;
    }

    public void setObInterval(Integer obInterval) {
        this.obInterval = obInterval;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public static class Rank {
        private Long fansRank;
        private Long archiveViewRank;
        private Long articleViewRank;
        private Long likeRank;
        private Long dFansRank;
        private Long dArchiveViewRank;
        private Long dArticleViewRank;
        private Long dLikeRank;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date updateTime;

        public Rank() {
        }

        public Rank(Long fansRank, Long archiveViewRank, Long articleViewRank, Long likeRank, Long dFansRank, Long dArchiveViewRank, Long dArticleViewRank, Long dLikeRank, Date updateTime) {
            this.fansRank = fansRank;
            this.archiveViewRank = archiveViewRank;
            this.articleViewRank = articleViewRank;
            this.likeRank = likeRank;
            this.dFansRank = dFansRank;
            this.dArchiveViewRank = dArchiveViewRank;
            this.dArticleViewRank = dArticleViewRank;
            this.dLikeRank = dLikeRank;
            this.updateTime = updateTime;
        }

        public Long getFansRank() {
            return fansRank;
        }

        public void setFansRank(Long fansRank) {
            this.fansRank = fansRank;
        }

        public Long getArchiveViewRank() {
            return archiveViewRank;
        }

        public void setArchiveViewRank(Long archiveViewRank) {
            this.archiveViewRank = archiveViewRank;
        }

        public Long getArticleViewRank() {
            return articleViewRank;
        }

        public void setArticleViewRank(Long articleViewRank) {
            this.articleViewRank = articleViewRank;
        }

        public Long getLikeRank() {
            return likeRank;
        }

        public void setLikeRank(Long likeRank) {
            this.likeRank = likeRank;
        }

        public Long getdFansRank() {
            return dFansRank;
        }

        public void setdFansRank(Long dFansRank) {
            this.dFansRank = dFansRank;
        }

        public Long getdArchiveViewRank() {
            return dArchiveViewRank;
        }

        public void setdArchiveViewRank(Long dArchiveViewRank) {
            this.dArchiveViewRank = dArchiveViewRank;
        }

        public Long getdArticleViewRank() {
            return dArticleViewRank;
        }

        public void setdArticleViewRank(Long dArticleViewRank) {
            this.dArticleViewRank = dArticleViewRank;
        }

        public Long getdLikeRank() {
            return dLikeRank;
        }

        public void setdLikeRank(Long dLikeRank) {
            this.dLikeRank = dLikeRank;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }
    }

    @Document(value = "author_data")
    public static class Data {
        @Id
        private ObjectId id;
        private Long mid;
        private Long fans;
        private Integer attention;
        private Integer archive;
        private Integer article;
        private Long archiveView;
        private Long articleView;
        private Long like;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date datetime;

        public Long getMid() {
            return mid;
        }

        public void setMid(Long mid) {
            this.mid = mid;
        }

        public Long getArchiveView() {
            return archiveView;
        }

        public void setArchiveView(Long archiveView) {
            this.archiveView = archiveView;
        }

        public Long getLike() {
            return like;
        }

        public void setLike(Long like) {
            this.like = like;
        }

        public Long getArticleView() {
            return articleView;
        }

        public void setArticleView(Long articleView) {
            this.articleView = articleView;
        }

        public Long getFans() {
            return fans;
        }

        public void setFans(Long fans) {
            this.fans = fans;
        }

        public Integer getAttention() {
            return attention;
        }

        public void setAttention(Integer attention) {
            this.attention = attention;
        }

        public Integer getArchive() {
            return archive;
        }

        public void setArchive(Integer archive) {
            this.archive = archive;
        }

        public Integer getArticle() {
            return article;
        }

        public void setArticle(Integer article) {
            this.article = article;
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }
    }

    private static class Channel {
        private Integer tid;
        private Integer count;
        private String name;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * @author Jannchie
     */
    @Document("author_achievement")
    public static class Achievement {
        private Date date;
        private Author author;
        private Long value;
        private Integer code;
        private Integer level;
        private String name;
        private String desc;

        public Achievement() {
        }

        public Achievement(AuthorAchievementEnum authorAchievementEnum, Author author) {
            this.value = authorAchievementEnum.getValue();
            this.level = authorAchievementEnum.getLevel();
            this.name = authorAchievementEnum.getName();
            this.code = authorAchievementEnum.getId();
            this.desc = authorAchievementEnum.getDesc();
            this.author = author;
        }

        public Achievement(AuthorAchievementEnum authorAchievementEnum, Long mid) {
            this.value = authorAchievementEnum.getValue();
            this.level = authorAchievementEnum.getLevel();
            this.name = authorAchievementEnum.getName();
            this.desc = authorAchievementEnum.getDesc();
            this.code = authorAchievementEnum.getId();
            Author a = new Author();
            a.setMid(mid);
            this.author = a;
        }

        public Achievement(Long mid, Long value, Integer code, Integer level, String name, String desc) {
            Author a = new Author();
            a.setMid(mid);
            this.author = a;
            this.value = value;
            this.code = code;
            this.level = level;
            this.name = name;
            this.desc = desc;
        }

        public Achievement(AuthorUniqueAchievementEnum authorAchievementEnum, Long mid, Long value) {
            this.level = authorAchievementEnum.getLevel();
            this.name = authorAchievementEnum.getName();
            this.code = authorAchievementEnum.getId();
            this.desc = authorAchievementEnum.getDesc();
            this.value = value;
            Author a = new Author();
            a.setMid(mid);
            this.author = a;
        }

        public Achievement(AuthorAchievementEnum authorAchievementEnum, Long mid, Long value) {
            this.level = authorAchievementEnum.getLevel();
            this.name = authorAchievementEnum.getName();
            this.code = authorAchievementEnum.getId();
            this.desc = authorAchievementEnum.getDesc();
            this.value = value;
            Author a = new Author();
            a.setMid(mid);
            this.author = a;
        }

        public Achievement(AuthorUniqueAchievementEnum e, Long mid) {
            this.value = e.getValue();
            this.level = e.getLevel();
            this.name = e.getName();
            this.desc = e.getDesc();
            this.code = e.getId();
            Author a = new Author();
            a.setMid(mid);
            this.author = a;
        }

        public Achievement(AuthorAchievementEnum achievementEnum, Long mid, Long value, Date datetime) {
            this.level = achievementEnum.getLevel();
            this.name = achievementEnum.getName();
            this.code = achievementEnum.getId();
            this.desc = achievementEnum.getDesc();
            this.date = datetime;
            this.value = value;
            Author a = new Author();
            a.setMid(mid);
            this.author = a;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

    }
}
