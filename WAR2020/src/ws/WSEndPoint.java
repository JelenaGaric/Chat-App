package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Singleton
@ServerEndpoint("/ws/{clientId}")
@LocalBean
public class WSEndPoint {
    private volatile Long clientId; 
	static HashMap<Long, Session> sessions = new HashMap<Long, Session>();

	@SuppressWarnings("deprecation")
	@OnOpen
	public void onOpen(@PathParam("clientId") String clientId, Session session) {
        this.clientId = new Long(clientId);
		if(!sessions.values().contains(session)) {
			sessions.put(this.clientId, session);
		}
	}
	
//	@OnMessage
//	public void echoTextMessage(Session session, String msg, boolean last) {
//		try {
//			if(session.isOpen()) {
//				for(Session s: sessions) {
//					if(!s.getId().equals(session.getId())) {
//						s.getBasicRemote().sendText(msg, last);
//					}
//				}
//			}
//		} catch (IOException e){
//			try {
//				session.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//	}
	@OnMessage
	public void echoTextMessage(String message) {
		
		ObjectMapper mapper = new ObjectMapper();
		Message msg;
		try {
			msg = mapper.readValue(message, Message.class);
			if(msg.getRecieverId().equals("all")) {
				//slanje poruke svima
				try {
					for(Session s: sessions.values()) {
						System.out.println("WSEndPoint: "+msg.getText());
						s.getBasicRemote().sendText(msg.getText());
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			} else {
				//slanje poruke jednom useru
				try {
					if(sessions.containsKey(msg.getRecieverId())) {
						sessions.get(msg.getRecieverId()).getBasicRemote().sendText(msg.getText());
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
//	@OnMessage
//	public void echoTextMessage(Long userId, String msg) {
//		try {
//			if(sessions.containsKey(userId)) {
//				sessions.get(userId).getBasicRemote().sendText(msg);
//			}
//		} catch (IOException e){
//			e.printStackTrace();
//		}
//	}
	
	@OnClose
	public void close(Session session) {
		sessions.remove(session);
	}
	
	@OnError
	public void error(Session session, Throwable t) {
		sessions.remove(session);
		t.printStackTrace();
	}
}
