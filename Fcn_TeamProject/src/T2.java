
import java.util.Scanner;


public class T2 {

	public static void main(String[]args)
	{
		System.out.println("Enter chat id");
		Scanner read=new Scanner(System.in);
		String name=read.next();
		ClientSender c2=new ClientSender(name,4002);
		c2.start();
		//read.close();
		}
}
