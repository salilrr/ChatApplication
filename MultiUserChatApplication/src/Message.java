import java.io.Serializable;
import java.util.Random;


public class Message implements Serializable {

	
	private static final long serialVersionUID = 1L;
	String source;
	String sourcePort;
	String destination;
	String destinationPort;
	String text;
	int messageID;
	byte[]dataPart;
	
	public Message()
	{
		this.source="";
		this.destination="";
		this.text="";
		Random r=new Random();
		this.messageID=r.nextInt(Integer.MAX_VALUE);
		
	}
	
	public Message(String source,String destination,String message,String sourcePort,String destinanationPort)
	{
		this.source=source;
		this.destination=destination;
		this.text=message;
		Random r=new Random();
		this.messageID=r.nextInt(Integer.MAX_VALUE);
		this.sourcePort=sourcePort;
		this.destinationPort=destinanationPort;
	}
}
