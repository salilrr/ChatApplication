import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;


public class ClientR  extends Thread{

	DatagramSocket soc;
	TreeMap<Integer,Message>dataFile;
	String myOwnName;

	public ClientR(int port,String myOwnName)
	{
		try {
			soc=new DatagramSocket(port);
			dataFile=new TreeMap<>();
			this.myOwnName=myOwnName;
			System.out.println("listening on port: "+port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run()
	{
		DatagramPacket packet;
		byte buffer[]=new byte[10000];
		ByteArrayInputStream bis;
		ObjectInputStream in;
		while(true)
		{
			packet=new DatagramPacket(buffer, buffer.length);

			try {
				soc.receive(packet);

				buffer=packet.getData();
				bis=new ByteArrayInputStream(buffer);
				in=new ObjectInputStream(bis);
				Message message=(Message)in.readObject();
				if(message.text.contains("data"))
				{
					processDataPacket(message);
					continue;
				}
				if(message.text.contains("Ack"))
				{
					System.out.println("Message recieved by : "+message.source+" message Id: "+message.text.substring(message.text.indexOf(":")+1));
					continue;
				}
				if(message.text.equals(""))
					continue;
				System.out.println(" Message from "+message.source+" Message:"+message.text);
				System.out.println("Sending Ack for message id:"+message.messageID);
				
				
				
				if(!message.text.contains("Ack"))
					sendAck(message,packet);

			} catch (IOException e) {
				System.out.println("IO exception");
			} catch (ClassNotFoundException e) {
				System.out.println("class not found.TypeCast");
			}
		}
	}

	private void processDataPacket(Message message) throws IOException {

		
		String[] text=message.text.split(":");
		Integer seqNumber=new Integer(text[1]);
	//	System.out.println("received datap "+seqNumber);
		if(seqNumber==Integer.MAX_VALUE)
		{
			File file =new File("from"+message.source+"to"+myOwnName+".txt");
			FileOutputStream fout=new FileOutputStream(file);
			Iterator<Integer>itr=dataFile.keySet().iterator();
			//Iterator<Integer>i=dataFile.keySet().iterator();
			
	
			while(itr.hasNext())
			{
				Message m=dataFile.get(itr.next());
				fout.write(m.dataPart);
				fout.flush();
			}
			fout.close();
			System.out.println("file transfer complete");
		}
		else
		{
			dataFile.put(seqNumber,message);
		}
	}
	
	

	private void sendAck(Message message,DatagramPacket packet) {

		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream os=new ObjectOutputStream(bos);
			Message ack=new Message();
			ack.destination=message.source;
			ack.source=myOwnName;
			ack.text="Ack: "+message.messageID;
			

			os.writeObject(ack);
			byte[]buff=bos.toByteArray();

			InetAddress ip=InetAddress.getByName(packet.getAddress().toString().substring(1));//(message.destination);
			int destPort=9940;//packet.getPort();

			DatagramPacket MessagePacket=new DatagramPacket(buff, buff.length,ip,destPort);	

			soc.send(MessagePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
