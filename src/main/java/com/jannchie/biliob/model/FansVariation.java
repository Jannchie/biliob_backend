package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "fans_variation")
public class FansVariation {
    private Integer mid;
    private Integer variation;
    private String author;
    private String face;
    private String info;
    private Integer rate;
    private String deltaRate;
    private String datetime;
    private Cause cause;

    public String getDeltaRate() {
        return deltaRate;
    }

    public Integer getVariation() {
        return variation;
    }

    public String getFace() {
        return face;
    }

    public String getInfo() {
        return info;
    }

    public Integer getMid() {
        return mid;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getRate() {
        return rate;
    }

    public String getDatetime() {
        return datetime;
    }

    public Cause getCause() {
        return cause;
    }

    private class Cause {
        private String type;
        private Integer aid;
        private String title;
        private String pic;
        private String channel;
        private String subChannel;

        public String getChannel() {
            return channel;
        }

        public String getPic() {
            return pic;
        }

        public String getSubChannel() {
            return subChannel;
        }

        public String getType() {
            return type;
        }

        public Integer getAid() {
            return aid;
        }

        public String getTitle() {
            return title;
        }
    }
}
