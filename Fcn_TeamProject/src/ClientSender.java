import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;


public class ClientSender extends Thread {

	String SERVERIP ="127.0.0.1";//"129.21.22.196";//"127.0.0.1";
	int SERVERPORT=9944;
	int SERVERMESSAGEPORT=9940;
	String serverconnectionPort;
	DatagramSocket sendingPort;
	String myOwnName;
	//ServerData serverData;
	String chatWith;
	//String chatWithPort;
	//int rPort;
	int clientPortListening;
	HashMap<String, Client>activeUsers;
	
	public ClientSender(String ownName,int clientPortListening)
	{
		//this.serverIP="127.0.0.1";
		
		try {
			this.sendingPort=new DatagramSocket();//(clientPortS);
			//this.serverData=sd;
			this.clientPortListening=clientPortListening;
			this.myOwnName=ownName;
					
		} catch (SocketException e) {
			System.out.println("Error while binding client.");
		}
	}
	
	public void run()
	{
		intitateServerConnection();
		ClientR cR=new ClientR(clientPortListening,myOwnName);
		cR.start();
		Scanner read=new Scanner(System.in);
		//System.out.println(serverData.activeUsers);
		System.out.println("chat with?");
		String temp=read.next();
		
		chatWith=temp;
		if(chatWith.contains("multicast"))
		{
			String user[]=chatWith.split(":");
			
			chatWith=user[1];
			
				
		}
		else
		if(chatWith.contains("broadcast"))
		{
			chatWith="all";
			
		}
	//	System.out.println("chat with port?");
		//chatWithPort=read.next();
		while(true)
		{
			System.out.println(">>>>");
			
			String text=read.nextLine();
			if(text.equalsIgnoreCase("exit"))
			{
				sendMessage("exit");
				System.exit(0);
				break;
			}
			else
			if(text.contains("user"))
			{
				String user[]=text.split(":");
				chatWith=user[1];
				//chatWithPort=user[2];
				continue;
			}
			else
			if(text.contains("multicast"))
			{
				
				String user[]=text.split(":");
				
				chatWith=user[1];
				continue;
					
			}
			else
			if(text.contains("broadcast"))
			{
				chatWith="all";
				continue;
			}
			if(text.contains("file"))
			{
				try {
					sendFile(text);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sendMessage(text);
			
		}
		//r.close();
		
	}

	private void sendFile(String text) throws IOException {
		String[] user=text.split(":");
		File sourceFile=new File(user[1]);
		
		LinkedHashMap<Integer,byte[]>dataPackets=new LinkedHashMap<>();
		FileInputStream fis=new FileInputStream(sourceFile);
		byte[]buffer=new byte[5000];
		int num=0;
		num=fis.read(buffer);
		int count=1;
		while(num!=-1)
		{
			dataPackets.put(count,buffer);
			buffer=new byte[5000];
			count++;
			num=fis.read(buffer);
		}
		dataPackets.put(Integer.MAX_VALUE, new byte[5]);
		
		Iterator<Integer>itr =dataPackets.keySet().iterator();
		
		int seqNumber;
		while(itr.hasNext())
		{
			seqNumber=itr.next();
			byte[] dataChunk=dataPackets.get(seqNumber);
			Message dataPacket=new Message();
			dataPacket.text="data:"+seqNumber;
			dataPacket.source=myOwnName;
			dataPacket.destination=chatWith;
			dataPacket.dataPart=dataChunk;
			
			try
			{
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream os=new ObjectOutputStream(bos);
		
			os.writeObject(dataPacket);
			byte[]buff=bos.toByteArray();
			InetAddress ip=InetAddress.getByName(SERVERIP);
			//int destPort=9940;//Integer.parseInt(serverconnectionPort);
			
			DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip ,SERVERMESSAGEPORT);
			
			sendingPort.send(MessagePacket);
			Thread.sleep(1000);
			} catch (IOException e) {
				System.out.println("io Exception while sending message.In sendMessage method of client sender");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
		}
		
	}

	private void sendMessage(String text) {
		
		
		Message m=new Message();
		m.text=text;
		m.source=myOwnName;
		m.destination=chatWith;
		try
		{
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ObjectOutputStream os=new ObjectOutputStream(bos);
	
		os.writeObject(m);
		byte[]buff=bos.toByteArray();
		InetAddress ip=InetAddress.getByName(SERVERIP);
		//int destPort=9940;//Integer.parseInt(serverconnectionPort);
		
		DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip ,SERVERMESSAGEPORT);
		
			sendingPort.send(MessagePacket);
		} catch (IOException e) {
			System.out.println("io Exception while sending message.In sendMessage method of client sender");
		}
	
	}

	@SuppressWarnings("unchecked")
	private void intitateServerConnection() 
	{
		Message m=new Message();
		m.sourcePort=new Integer(clientPortListening).toString();
		m.text=this.myOwnName;
		
			try
			{
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream os=new ObjectOutputStream(bos);
		
			os.writeObject(m);
			byte[]buff=bos.toByteArray();
			InetAddress ip=InetAddress.getByName(SERVERIP);
			//int destPort=9944;//Integer.parseInt(serverconnectionPort);
			
			DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip,SERVERPORT);	
			sendingPort.send(MessagePacket);
		
			byte buffer[]=new byte[10000];
			DatagramPacket packet=new DatagramPacket(buffer, buffer.length);
			sendingPort.receive(packet);	
			buffer=packet.getData();
			ByteArrayInputStream bis=new ByteArrayInputStream(buffer);
			ObjectInputStream in=new ObjectInputStream(bis);
			activeUsers=(HashMap<String, Client>)in.readObject();
			System.out.println(activeUsers.keySet());
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("io Exception while sending message.In intiateserver connection of client");
			}
		
	}
}
