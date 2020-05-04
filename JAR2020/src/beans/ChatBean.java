package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hosts.Hosts;
import model.Host;
import model.Message;
import model.ROLE;
import model.User;
import utils.FileUtils;

@Stateless
@Path("/chat")
@LocalBean
public class ChatBean {
	
	private ArrayList<User> loggedInUsers = new ArrayList<User>();
	private ArrayList<Message> chatRoomMessages = new ArrayList<Message>();
	private String master = null;
	private String nodeName;
	private String nodeAddress;

	@EJB
	private Hosts hosts;
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(mappedName = "java:/jboss/exported/jms/queue/mojQueue")
	private Queue queue;
	
	//************************************************Pravljenje hosta****************************************************************
	@PostConstruct
	public void postConstruct() {
		System.out.println("Novi host");
		 InetAddress ip;
	     String hostname;
	        try {
	            ip = InetAddress.getLocalHost();
	            hostname = ip.getHostName();
	            System.out.println("Your current IP address : " + ip);
	            System.out.println("Your current Hostname : " + hostname);
	            Host host = new Host(ip.getHostAddress(), ip.getHostName());
	            
//	        	MBeanServer mbServer = ManagementFactory.getPlatformMBeanServer();
//				ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
//				String nodeAddress = (String) mbServer.getAttribute(http, "boundAddress");
//				String nodeName = System.getProperty("jboss.node.name") + ":8080";
//				Host host = new Host(nodeAddress, nodeName);
				System.out.println("Novi host, alias: " + host.getAlias() + ", adresa:" + host.getAdress());
				
	            File f = FileUtils.getFile(ChatBean.class, "", "connection.properties");
				FileInputStream fileInput = new FileInputStream(f);
				Properties properties = new Properties();
	            properties.load(fileInput);
				fileInput.close();
				this.master = properties.getProperty("master");
				System.out.println("Master : " + master);
				
				if (master != null && !master.equals("")) {
					System.out.println("Already have a master host, handshake method in process...");
					handshake(host);
				} else {
					hosts.setMasterHost(host);
					//dodati u listu hostova
					hosts.getHosts().add(host);
				}
				//postaviti trenutni host
				hosts.setCurrentHost(host);
	        } catch (UnknownHostException e) {
	 
	            e.printStackTrace();
	        } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//			} catch (MalformedObjectNameException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InstanceNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (AttributeNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ReflectionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (MBeanException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		
	}
	
	public void handshake(Host host) {
		registerHost(host);
		
	}
	
	public void registerHost(Host host) {
		System.out.println("Register for host with ip: " + host.getAdress());
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + this.master + ":8080/WAR2020/rest/chat/registerHost");
		Response response = target.request().post(Entity.entity(host, "application/json"));
		client.close();
		System.out.println("Host registered");
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/registerHost")
	public Response newHost(Host host) {
		for(Host h: hosts.getHosts()) {
			if(!h.getAdress().equals(hosts.getCurrentHost().getAdress())) {
				//slanje rest zahtjeva svim ostalim hostovima da dodaju novog u listu
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + h.getAdress() + ":8080/WAR2020/rest/chat/node");
				Response responseNode = target.request().post(Entity.entity(host, "application/json"));
				client.close();
				System.out.println("added host with ip "+ host.getAdress() +" to host with ip " + h.getAdress());
			}
		}
		hosts.getHosts().add(host);
		System.out.println("host registered within all hosts");
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + host.getAdress() + ":8080/WAR2020/rest/chat/nodes");
		Response response = target.request().post(Entity.entity(this.hosts.getHosts(), "application/json"));
		client.close();
		System.out.println("sent nodes to new host!");
		
		sendLoggedIn(host);
		return Response.status(200).build();
	
	}	
	
	public void sendLoggedIn(Host host) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + host.getAdress() + ":8080/WAR2020/rest/chat/node/users/loggedIn");
		Response response = target.request().post(Entity.entity(loggedInUsers, "application/json"));
		System.out.println("sent logged in list to host with ip: " + host.getAdress());
		client.close();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node")
	public Response addHost(Host host) {
		hosts.getHosts().add(host);
		System.out.println("host added for host " + hosts.getCurrentHost().getAdress());
		return Response.status(200).build();
	
	}	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node/users/loggedIn")
	public Response sendLoggedIn(ArrayList<User> loggedIn) {
		this.loggedInUsers = loggedIn;
		System.out.println("set logged in users for host " + hosts.getCurrentHost().getAdress());
		return Response.status(200).build();
	}	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/nodes")
	public Response sendNodesToHost(ArrayList<Host> nodes) {
		hosts.setHosts(nodes);
		System.out.println("set hosts for host: " + hosts.getCurrentHost().getAdress());
		return Response.status(200).build();
	
	}	
	//*******************************************************************************************************************************
	
	@POST
	@Path("/users/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User u) {
		System.out.println("Tried to log in - user: " + u.getUsername());
		
		HashMap<Long, User> users = loadUsers();
		User loggedIn = null;
		
		for(User user: users.values()) {
			if(u.getUsername().equals(user.getUsername())) {
				if(u.getPassword().equals(user.getPassword())) {
					if(loggedInUsers.contains(users)) {
						//vec je ulogovan , samo nastavi
						return Response.ok(user, MediaType.APPLICATION_JSON).build();
					} else {
						loggedIn = user;
					}
				}	
			}
		}
		

		//ako ne postoji korisnik
		if(loggedIn == null) {
			return Response.status(400).build();
		}
		
		//ako postoji, dodaj u listu ulogovanih
		loggedInUsers.add(loggedIn);
		
		if(loggedInUsers.size()==0) {
			System.out.println("No logged in users.");
		}
		for(User us: loggedInUsers) {
			System.out.println("logged in users: "+us.getUsername());
		}
		
		//slanje poruke svima preko mdb da se ulogovao novi korisnik
		Message loginMsg = new Message("all", String.valueOf(loggedIn.getId()), "[System]:logged in[" + String.valueOf(loggedIn.getId())+"]", new Date());
		ObjectMapper mapper = new ObjectMapper();
		String loginJson;
		try {
			loginJson = mapper.writeValueAsString(loginMsg);

			sendMessage(loginJson);
			
			return Response.ok(loggedIn, MediaType.APPLICATION_JSON).build();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(400).build();

	}
	

	@SuppressWarnings("deprecation")
	@POST
	@Path("/users/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User u) {
		System.out.println("Tried to register - user: " + u.getUsername());
			
		try{
			HashMap<Long, User> users = loadUsers();
			for(User user: users.values()) {
				if(u.getUsername().equals(user.getUsername())) {
					//vec postoji
					return Response.status(Response.Status.BAD_REQUEST).entity("Username is taken.").build();
				}
			}
			
			u.setRole(ROLE.USER);
			u.setId(new Long(users.size()+1));
			users.put(new Long(users.size()+1), u);
			
			try{
				saveUsers(users);
				return Response.status(200).build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(400).build();
	}
	
	@DELETE
	@Path("/users/loggedIn/{user}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam ("user") String username) {
		System.out.println("Tried to log out - user: " + username);
		User user = null;
		
		for(User u: loggedInUsers) {
			if(u.getUsername().equals(username)) {
				user = u;
			}
		}
		
		if(user == null) {
			return Response.status(400).build();
		} else {
			Message logoutMsg = new Message("all", String.valueOf(user.getId()), "[System]:logged out[" + String.valueOf(user.getId())+"]", new Date());
			ObjectMapper mapper = new ObjectMapper();
			String logoutJson;
			try {
				logoutJson = mapper.writeValueAsString(logoutMsg);
				//slanje poruke svim sessionima preko mdb da se neko izlogovao
				sendMessage(logoutJson);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			loggedInUsers.remove(user);
			
			//brisanje poruka ove sesije?
			
			if(loggedInUsers.size()==0) {
				System.out.println("No logged in users.");
			} else {
				for(User u: loggedInUsers) {
					System.out.println("logged in: " + u.getUsername());
				}
			}
			
		}
		return Response.status(200).build();
		
	}
	
	@GET
	@Path("/user/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam ("id") Long id) {
		HashMap<Long, User> users = loadUsers();
		return Response.ok(users.get(id), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/users/registered")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registeredUsers() {
		HashMap<Long, User> users = loadUsers();
		return Response.ok(users.values(), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/users/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loggedInUsers() {
		return Response.ok(loggedInUsers, MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("/messages/all")
	@Produces(MediaType.TEXT_PLAIN)
	public String post(String text) {
		System.out.println("Recieved message (for all): " + text);
		HashMap<Long, User> users = loadUsers();
		Message message = stringToMesage(text);
		users.get(new Long(message.getSenderId())).getMsgs().add(message);
		sendMessage(text);
		saveUsers(users);
		chatRoomMessages.add(message);
		return "OK";
	}
	
	@POST
	@Path("/messages/toUser")
	@Produces(MediaType.TEXT_PLAIN)
	public String sendMessageToUser(String msg) {
		System.out.println("Recieved message: " + msg);
		HashMap<Long, User> users = loadUsers();
		Message message = stringToMesage(msg);
		users.get(new Long(message.getSenderId())).getMsgs().add(message);
		users.get(new Long(message.getRecieverId())).getMsgs().add(message);
		sendMessage(msg);
		saveUsers(users);
		return "OK";
	}
	
	@GET
	@Path("/messages/chatroom")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChatroomMessages(String msg) {
		return Response.ok(chatRoomMessages, MediaType.APPLICATION_JSON).build();
	}
	
	private Message stringToMesage(String messageString) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(messageString, Message.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Message();
	}
	
	@GET
	@Path("/messages/{id}/{loggedInId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessagesWitId(@PathParam ("id") Long id, @PathParam ("loggedInId") Long loggedInId) {
		ArrayList<Message> messages = new ArrayList<Message>();
		
		HashMap<Long, User> users = loadUsers();
		User loggedIn = users.get(loggedInId);
		User u = users.get(id);
		System.out.println(u.getUsername() + " i " + loggedIn.getUsername());
		for(Message m : loggedIn.getMsgs()) {
			if(!m.getRecieverId().equals("all")) {
				if(new Long(m.getRecieverId()).equals(u.getId())) {
					messages.add(m);
					System.out.println("PORUKA "+m.getText());
				}
			}
		}
		for(Message m : u.getMsgs()) {
			if(!m.getRecieverId().equals("all")) {
				if(new Long(m.getRecieverId()).equals(loggedIn.getId())) {
					System.out.println("PORUKA "+m.getText());
	
					messages.add(m);
				}
			}
		}
		return Response.ok(messages, MediaType.APPLICATION_JSON).build();
	}
	
	
	//******************************************* Slanje poruke MDB-u *******************************************
	private void sendMessage(String text) {
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
	}
	
	
	//******************************************* Ucitavanje i cuvanje u "bazu" *******************************************
	private HashMap<Long, User> loadUsers() {
			
			HashMap<Long, User> users = new HashMap<Long, User>();
		
			//String path = "D:\\agentske tehnologije\\eclipse-ws\\JAR2020\\src\\baza.txt";
			 
			String path = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "baza.txt";
			
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
			BufferedReader in = null;
			try
			{
				file = new File(path);
				
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
			//String path = "D:\\agentske tehnologije\\eclipse-ws\\JAR2020\\src\\baza.txt";
			
			String path = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "baza.txt";
			
			
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
