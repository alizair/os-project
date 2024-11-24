package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class ServerService implements Runnable {
	
	Socket nextClient;
	
	public ServerService(Socket nextClient) {
		this.nextClient = nextClient;
	}
	
	public void run() {
		try {
			// Display connection details
			System.out.println("Receiving Request From " + 
			nextClient.getInetAddress() + ":" +
			nextClient.getPort());
			ProcessBuilder processBuilder = new ProcessBuilder();
			ProcessBuilder processBuilder2;
			processBuilder.command("./network.sh", "10.10.10.129", "10.10.10.130");
			processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));
			String input = reader.readLine();
			if (input.equalsIgnoreCase("SYSINFO")) {
				processBuilder2 = new ProcessBuilder("./system.sh");
				File outputFile = new File("system_info.txt");
				processBuilder2.redirectOutput(outputFile);
				Process process = processBuilder2.start();
				int exitCode = process.waitFor();
				if (exitCode == 0) {
					System.out.println("Script executed successfully. Output saved to system_info.txt");
		        } else {
		        	System.out.println("Script execution failed. Exit code: " + exitCode);
		        }
				PrintWriter output = new PrintWriter(nextClient.getOutputStream(), true);
				output.println(outputFile);
			}
			nextClient.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}

