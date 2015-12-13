import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class ServerData {

	Queue<Message>messages;
	Queue<Ack>acks;
	//ArrayList<Client>activeUsers;
	//ArrayList<Client>users;
	Queue<Message>bufferedMessages;
	boolean closeServer;
	HashMap<String,Client>userMap;
	HashMap<String,Client>activeUser;
	
	
	public ServerData()
	{
		this.messages=new LinkedList<>();
		this.acks=new LinkedList<>();
		this.bufferedMessages=new LinkedList<>();
		//this.activeUsers=new ArrayList<>();
		//this.users=new ArrayList<>();
		this.userMap=new HashMap<>();
		this.activeUser=new HashMap<>();
		this.closeServer=false;
		
		for(int i=1;i<10;i++)
		{
			userMap.put("T"+i,null);
		}
	}
}
