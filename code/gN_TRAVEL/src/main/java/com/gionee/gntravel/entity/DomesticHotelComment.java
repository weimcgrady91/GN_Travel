package com.gionee.gntravel.entity;
/**
 * @Author:yangxy
 * @Create 2014-7-7
 */
public class DomesticHotelComment {
	private String rating;
	private String userId;
	private String writingDate;
	private String commentSubject;
	private String content;
	private String identityTxt;
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getWritingDate() {
		return writingDate;
	}
	public void setWritingDate(String writingDate) {
		this.writingDate = writingDate;
	}
	public String getCommentSubject() {
		return commentSubject;
	}
	public void setCommentSubject(String commentSubject) {
		this.commentSubject = commentSubject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getIdentityTxt() {
		return identityTxt;
	}
	public void setIdentityTxt(String identityTxt) {
		this.identityTxt = identityTxt;
	}

}

