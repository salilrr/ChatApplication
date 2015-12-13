import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class ReceiveMessages extends Thread {

	ServerData serverData;
	DatagramSocket receiveMessage;
	
	public ReceiveMessages(ServerData serverData)
	{
		this.serverData=serverData;
		try {
			this.receiveMessage=new DatagramSocket(9940);
		} catch (SocketException e) {
			
			System.out.println("Could not bind to socket.");
		}
	}
	
	public void run()
	{
		System.out.println("message reciever of server started");
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
				receiveMessage.receive(packet);	
				buffer=packet.getData();
				bis=new ByteArrayInputStream(buffer);
				in=new ObjectInputStream(bis);
				Message m=(Message)in.readObject();
				if(m.text.contains("exit"))
				{
					serverData.activeUser.remove(m.source);
					continue;
				}
				System.out.println("Source:"+m.source+" Destination: "+m.destination+"\ntext: "+m.text);
				serverData.messages.offer(m);
				
			} catch (IOException e) {
				System.out.println("IO exception");
			} catch (ClassNotFoundException e) {
				System.out.println("class not found.TypeCast");
			}
		}
	}
}
