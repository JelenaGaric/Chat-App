package model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String username;
	private String password;
	private ROLE role;
	private ArrayList<Message> msgs = new ArrayList<>();
	
	private Host host;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public ROLE getRole() {
		return role;
	}

	public void setRole(ROLE role) {
		this.role = role;
	}

	
	public ArrayList<Message> getMsgs() {
		return msgs;
	}

	public void setMsgs(ArrayList<Message> msgs) {
		this.msgs = msgs;
	}
	
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public User(Long id, String username, String password, ROLE role) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	public User(Long id, String username, String password, ROLE role, ArrayList<Message> msgs, Host host) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.msgs = msgs;
		this.host = host;
	}

	@Override
	public String toString() {
		return "User: " + username;
	}

	public User() {
		
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
