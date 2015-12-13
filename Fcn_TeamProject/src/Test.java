
public class Test {

	public static void main(String[] args) {

		ServerData sd=new ServerData();
		ReceiveConnection rc=new ReceiveConnection(sd);
		ReceiveMessages rm=new ReceiveMessages(sd);
		SendMessages sm=new SendMessages(sd);
		rc.start();
		rm.start();
		sm.start();
		
	}

}
