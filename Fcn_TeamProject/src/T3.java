
import java.util.Scanner;


public class T3 {

	public static void main(String[]args)
	{
		Scanner read=new Scanner(System.in);
		String name=read.next();
		ClientSender c3=new ClientSender(name,4004);
		c3.start();
		}

}
