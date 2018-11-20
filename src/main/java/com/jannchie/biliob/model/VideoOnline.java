package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoOnline {
	private ObjectId id;

	private String title;

	private String author;

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public class Data {
		private String number;

		private Date datetime;

		public String getNumber() {
			return number;
		}

		public Date getDatetime() {
			return datetime;
		}
	}
}
