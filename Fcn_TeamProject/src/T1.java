import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JTextArea;


public class T1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Enter chat id");
		Scanner read=new Scanner(System.in);
		String name=read.next();
		Random r=new Random();
		int n=r.nextInt(10000);
		n+=r.nextInt(800);
		new ClientSender(name,n).start();
		//ClientSender c1=new ClientSender(name,4000);
		//c1.start();
		//read.close();
	}

}
