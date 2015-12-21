

public class Ack {

	int messageID;
	String source;
	String destination;
	
	public Ack(int messageID,String source,String destination)
	{
		this.messageID=messageID;
		this.source=source;
		this.destination=destination;
	}
}
