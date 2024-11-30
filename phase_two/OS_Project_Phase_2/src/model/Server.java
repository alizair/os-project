package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {
	
	private static final ArrayList<String> clientRequests = new ArrayList<>();
	
	public static void main(String args[]) {
		try {
			ServerSocket server = new ServerSocket(1300);
			ArrayList<Thread> threads = new ArrayList<>();
			System.out.println("Server waiting for client on port " +
			server.getLocalPort());
			System.out.println("Server Service Started");
			for(;;) {
				Socket nextClient = server.accept();
				Thread serviceThread = new Thread(new ServerService(nextClient));
				serviceThread.start();
				threads.add(serviceThread);
			}
		} catch (IOException ioe) {
			System.out.println("Error" + ioe);
		}
	}
	
	public static void logClientRequest(String clientInfo, String request) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String formattedDate = formatter.format(new Date());
	    clientRequests.add(String.format("%s requested: %s at %s", clientInfo, request, formattedDate));
	}
	
	public static void displayClientRequests() {
		System.out.println("Client Requests:");
		System.out.println(clientRequests.toString());
	}

}

