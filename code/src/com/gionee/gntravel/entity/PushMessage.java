package com.gionee.gntravel.entity;

public class PushMessage {
private String title;
private String time;
private String content;
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
@Override
public String toString() {
	return "Message [title=" + title + ", time=" + time + ", content="
			+ content + "]";
}


}