package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {

  private Long mid;
	private String name;
	private String face;
	private String sex;
	private String official;
  private Integer level;
  private ArrayList<Data> data;
  private ArrayList<Channel> channels;
  private Boolean focus;
  private Boolean forceFocus;


	public Author() {
		this.focus = true;
	}

	public Author(Long mid) {
		this.mid = mid;
		this.focus = true;
	}

  public void setFocus(Boolean focus) {
    this.focus = focus;
  }

  public Boolean getForceFocus() {
    return forceFocus;
  }

  public void setForceFocus(Boolean forceFocus) {
    this.forceFocus = forceFocus;
  }

  public ArrayList<Channel> getChannels() {
		return channels;
	}

	public void setChannel(ArrayList<Channel> channel) {
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

	private class Data {
		private Integer fans;
		private Integer attention;
		private Integer archive;
		private Integer article;
    private Integer archiveView;
    private Integer articleView;
		private Date datetime;

    public void setFans(Integer fans) {
      this.fans = fans;
    }

    public void setAttention(Integer attention) {
      this.attention = attention;
    }

    public void setArchive(Integer archive) {
      this.archive = archive;
    }

    public void setArticle(Integer article) {
      this.article = article;
    }

    public void setArchiveView(Integer archiveView) {
      this.archiveView = archiveView;
    }

    public void setArticleView(Integer articleView) {
      this.articleView = articleView;
    }

    public void setDatetime(Date datetime) {
      this.datetime = datetime;
    }

    public Integer getArchiveView() {
      return archiveView;
    }

    public Integer getArticleView() {
      return articleView;
    }

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

  private class Channel {
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
}
