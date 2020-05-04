package model;

import java.util.Date;

public class Message {
	private String recieverId;
	private String senderId;
	private String text;
	private Date time;
	
	public Message() {
		super();
	}
	
	public Message(String recieverId, String senderId, String text) {
		super();
		this.recieverId = recieverId;
		this.senderId = senderId;
		this.text = text;
	}
	
	public Message(String recieverId, String senderId, String text, Date time) {
		super();
		this.recieverId = recieverId;
		this.senderId = senderId;
		this.text = text;
		this.time = time;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getRecieverId() {
		return recieverId;
	}
	public void setRecieverId(String recieverId) {
		this.recieverId = recieverId;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	
	
}
