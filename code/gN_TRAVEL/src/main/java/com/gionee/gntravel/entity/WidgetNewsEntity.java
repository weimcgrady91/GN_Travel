package com.gionee.gntravel.entity;

import java.io.Serializable;

import android.net.Uri;

@SuppressWarnings("serial")
public class WidgetNewsEntity implements Serializable {
	public String long_title;
	public String pic;
	public String pubDate;
	public String comment;
	public String link;
	public String id;
	public Uri uri;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLong_title() {
		return long_title;
	}

	public void setLong_title(String long_title) {
		this.long_title = long_title;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

}
