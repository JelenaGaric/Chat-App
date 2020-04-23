package model;

public class Message {
	private String recieverId;
	private String senderId;
	private String text;
	
	public Message() {
		super();
	}
	
	public Message(String recieverId, String senderId, String text) {
		super();
		this.recieverId = recieverId;
		this.senderId = senderId;
		this.text = text;
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
