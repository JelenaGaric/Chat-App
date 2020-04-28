package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.EncodeException;
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
				//zatvori sesiju ako je logout
				if(msg.getText().startsWith("[System]:logged out")){
					System.out.println("Prije brisanja: " + sessions.size());
					close(sessions.get(new Long(msg.getSenderId())));
					System.out.println("Nakon brisanja: " + sessions.size());
					sendToAll(msg.getText());
					return;
				}
				//slanje poruke svima
				sendToAll(msg.getText());
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
	
	public void sendToAll(String text) {
		try {
			for(Session s: sessions.values()) {
				System.out.println("WSEndPoint: "+ text);
				s.getBasicRemote().sendText(text);
			}
		} catch (IOException e){
			e.printStackTrace();
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
		// Get the iterator over the HashMap 
        Iterator<HashMap.Entry<Long, Session> > iterator = sessions.entrySet().iterator(); 
  
        // Iterate over the HashMap 
        while (iterator.hasNext()) { 
        	// Get the entry at this iteration 
        	HashMap.Entry<Long, Session> entry = iterator.next(); 
        	// Check if this value is the required value 
            if (session.equals(entry.getValue())) { 
            	// Remove this entry from HashMap 
                iterator.remove(); 
            } 
        } 
  
        // Print the modified HashMap 
        System.out.println("Modified HashMap: " + sessions); 
	}
	
	@OnError
	public void error(Session session, Throwable t) {
		sessions.remove(session);
		t.printStackTrace();
	}
}
