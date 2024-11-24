package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {
	
	public static void main(String args[]) {
		try {
			Socket service = new Socket("10.10.10.131", 1300);
			Scanner sc = new Scanner(System.in);
			System.out.println("Connected with server " +
			service.getInetAddress() + ":" +
			service.getPort());
			service.setSoTimeout(2000);
			PrintWriter output = new PrintWriter(service.getOutputStream(), true);
			System.out.println("Enter a command: ");
			String input = sc.nextLine();
			output.println(input);
			BufferedReader reader = new BufferedReader(new InputStreamReader(service.getInputStream()));
			String file = reader.readLine();
			System.out.println(file);
			service.close();
		} catch(IOException ioe) {
			System.out.println("Error" + ioe);
		}
	}

}
