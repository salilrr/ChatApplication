import java.io.Serializable;


public class Client implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ipAddress;
	String portNumber;
	String name;
	
	public Client()
	{
		
	}
	public Client(String ip,String port)
	{
		this.ipAddress=ip;
		this.portNumber=port;
	}
	
	public String toString()
	{
		return "ip: "+this.ipAddress+", port:"+portNumber+"\n";
	}
	
	public boolean equals(Object o)
	{
		Client temp=(Client)o;
		
		
		return this.name.equals(temp.name);
	}
	
	public int hashCode()
	{
		return this.name.hashCode();
	}
}
