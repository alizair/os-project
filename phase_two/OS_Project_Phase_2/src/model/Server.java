package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	
	public static void main(String args[]) {
		try {
			ServerSocket server = new ServerSocket(1300);
			ArrayList<Thread> threads = new ArrayList<>();
			System.out.println("Server waiting for client on port " +
			server.getLocalPort());
			System.out.println("Server Service Started");
			for(;;) {
				// Get the next TCP Client
				Socket nextClient = server.accept();
				Thread serviceThread = new Thread(new ServerService(nextClient));
				serviceThread.start();
				threads.add(serviceThread);
			}
		} catch (IOException ioe) {
			System.out.println("Error" + ioe);
		}
	}

}

