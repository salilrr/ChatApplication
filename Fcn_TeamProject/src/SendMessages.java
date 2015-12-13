import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;


public class SendMessages extends Thread {

	ServerData serverData;
	DatagramSocket sendMessage;

	public SendMessages(ServerData serverData)
	{
		this.serverData=serverData;
		try {
			this.sendMessage=new DatagramSocket(9942);
		} catch (SocketException e) {

			System.out.println("Could not bind to socket.");
		}
	}

	public void run()
	{
		System.out.println("message sender of server started");
		while(true)
		{
			if(serverData.closeServer)
				break;
			//try sending messages buffered
			while(!serverData.messages.isEmpty())
			{
				Message message=serverData.messages.poll();
				String dest=message.destination;
				//String source=message.source;
				if(dest.contains("all"))
				{
					
					broadCast(message);
					continue;
				}
				else
				if(dest.contains(","))
				{
					multiCast(message);
					continue;
				}
				
				//Client sender=serverData.userMap.get(source);
				Client receiver=serverData.activeUser.get(dest);
				//check if user is active
				if(receiver!=null)
				{
					try
					{
						ByteArrayOutputStream bos=new ByteArrayOutputStream();
						ObjectOutputStream os=new ObjectOutputStream(bos);

						os.writeObject(message);
						byte[]buff=bos.toByteArray();
						
						InetAddress ip=InetAddress.getByName(receiver.ipAddress.substring(1));//(message.destination);
						int destPort=Integer.parseInt(receiver.portNumber);

						DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip,destPort);	
						sendMessage.send(MessagePacket);
					} catch (IOException e) {
						System.out.println("io Exception while sending message.In run Method of SendMessage");
					}
				}
				else
				{
					
					serverData.bufferedMessages.offer(message);
				}
				

			}
			//sending  buffered messages
			serverData.bufferedMessages.offer(null);		
			while(!serverData.bufferedMessages.isEmpty())
			{

				Message message=serverData.bufferedMessages.poll();
				if(message==null)
					break;
				String dest=message.destination;


				Client receiver=serverData.activeUser.get(dest);
				//check if user is active
				if(receiver!=null)
			
				{
					try
					{
						ByteArrayOutputStream bos=new ByteArrayOutputStream();
						ObjectOutputStream os=new ObjectOutputStream(bos);

						os.writeObject(message);
						byte[]buff=bos.toByteArray();
						InetAddress ip=InetAddress.getByName(receiver.ipAddress.substring(1));
						int destPort=Integer.parseInt(receiver.portNumber);

						DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip ,destPort);

						sendMessage.send(MessagePacket);
					} catch (IOException e) {
						System.out.println("io Exception while sending message.In run Method of SendMessage");
					}
				}
				else
				{
					serverData.bufferedMessages.offer(message);
				}

			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void multiCast(Message message) {

		String[]users=message.destination.split(",");
		
		for(String user:users)
		{
			System.out.println(user);
			Client receiver=serverData.activeUser.get(user);
			//check if user is active
			if(receiver!=null)
			{
				try
				{
					ByteArrayOutputStream bos=new ByteArrayOutputStream();
					ObjectOutputStream os=new ObjectOutputStream(bos);

					os.writeObject(message);
					byte[]buff=bos.toByteArray();
					
					InetAddress ip=InetAddress.getByName(receiver.ipAddress.substring(1));//(message.destination);
					int destPort=Integer.parseInt(receiver.portNumber);

					DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip,destPort);	
					sendMessage.send(MessagePacket);
				} catch (IOException e) {
					System.out.println("io Exception while sending message.In run Method of SendMessage");
				}
			}
			else
			{
				message.destination=user;
				serverData.bufferedMessages.offer(message);
			}

		}
	}

	private void broadCast(Message message) {

		Iterator<String>itr=serverData.activeUser.keySet().iterator();
		while(itr.hasNext())
		{
		
		String currUser=itr.next();
		if(message.source.equals(currUser))
			continue;
		Client receiver=serverData.activeUser.get(currUser);
		//check if user is active
		if(receiver!=null)
		{
			try
			{
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				ObjectOutputStream os=new ObjectOutputStream(bos);

				os.writeObject(message);
				byte[]buff=bos.toByteArray();
				
				InetAddress ip=InetAddress.getByName(receiver.ipAddress.substring(1));//(message.destination);
				int destPort=Integer.parseInt(receiver.portNumber);

				DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip,destPort);	
				sendMessage.send(MessagePacket);
			} catch (IOException e) {
				System.out.println("io Exception while sending message.In run Method of SendMessage");
			}
		}
		else
		{
			message.destination=currUser;
			serverData.bufferedMessages.offer(message);
		}
		}

	}
}
