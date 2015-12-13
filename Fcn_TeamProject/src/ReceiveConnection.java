import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class ReceiveConnection extends Thread {

	ServerData serverData;
	DatagramSocket receiveConnection;

	public ReceiveConnection(ServerData serverData)
	{
		this.serverData=serverData;
		try {
			this.receiveConnection=new DatagramSocket(9944);
		} catch (SocketException e) {

			System.out.println("Could not bind to socket.");
		}
	}

	public void run()
	{
		System.out.println("connection receiver of server started");
		DatagramPacket packet;
		byte buffer[]=new byte[10000];
		ByteArrayInputStream bis;
		ObjectInputStream in;
		while(true)
		{
			if(serverData.closeServer)
				break;
			packet=new DatagramPacket(buffer, buffer.length);

			try {
				
				receiveConnection.receive(packet);	
				buffer=packet.getData();
				bis=new ByteArrayInputStream(buffer);
				in=new ObjectInputStream(bis);
				Message message=(Message)in.readObject();

				Client newClient=new Client();
				newClient.ipAddress=packet.getAddress().toString();
				newClient.portNumber=message.sourcePort;
				newClient.name=message.text;
				//serverData.activeUsers.add(newClient);
			
				//sending list of active users;
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				ObjectOutputStream os=new ObjectOutputStream(bos);
				os.writeObject(serverData.userMap);
				byte[]buff=bos.toByteArray();
				InetAddress ip=packet.getAddress();
				//int destPort=Integer.parseInt(message.destinationPort)
				DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip ,packet.getPort());
				receiveConnection.send(MessagePacket);
				serverData.userMap.put(newClient.name, newClient);
				serverData.activeUser.put(newClient.name, newClient);
				//System.out.println(serverData.userMap);

			} catch (IOException e) {
				System.out.println("IO exception");
			} catch (ClassNotFoundException e) {
				System.out.println("class not found.TypeCast");
			}
		}
	}
}
