package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;

@Stateless
@Path("/chat")
@LocalBean
public class ChatBean {
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(mappedName = "java:/jboss/exported/jms/queue/mojQueue")
	private Queue queue;

	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "OK";
	}
	
	@POST
	@Path("/post/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public String post(@PathParam ("text") String text) {
		System.out.println("Recieved message: " + text);
		
		try {
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(queue);
			TextMessage message = session.createTextMessage();
			message.setText(text);
			sender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "OK";
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User u) {
		System.out.println("Tried to log in - user: " + u.getUsername());
		
//		try {
//			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
//			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
//			QueueSender sender = session.createSender(queue);
//			TextMessage message = session.createTextMessage();
//			message.setText(text);
//			sender.send(message);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		//return Response.status(400).build();
		
		try{
			HashMap<Long, User> users = loadUsers();
			for(User user: users.values()) {
				if(u.getUsername().equals(user.getUsername())) {
					if(u.getPassword().equals(user.getPassword())) {
						return Response.status(200).build();
					} else {
						return Response.status(400).build();
					}
				}
			}
			return Response.status(400).build();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(400).build();

	}
	
	private HashMap<Long, User> loadUsers() {
			
			HashMap<Long, User> users = new HashMap<Long, User>();
		
			String path = "D:\\agentske tehnologije\\eclipse-ws\\JAR2020\\src\\baza.txt";
			
			BufferedReader in = null;
			try
			{
				File file = new File(path);
				
				in = new BufferedReader(new FileReader(file));
				ObjectMapper mapper = new ObjectMapper();
				String line;		
				while((line = in.readLine()) != null)
				{
					User u = mapper.readValue(line, User.class);
					users.put(u.getId(), u);
				
				}	
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally
			{
				if(in != null)
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
			}
			
			return users;
		}
		
			
	public void saveUsers(HashMap<Long, User> users) {
		{
			String path = "./baza.txt";
			
			File file = new File(path);
			if(!file.exists())
			{
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				PrintWriter printer = new PrintWriter(path);
				ObjectMapper mapper = new ObjectMapper();
				for(User u : users.values())
				{	
					String jsonUser = mapper.writeValueAsString(u);
					printer.print(jsonUser);
					printer.println();
				}
				
				printer.flush();
				printer.close();
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
