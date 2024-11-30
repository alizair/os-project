package model;

import java.io.*;
import java.net.Socket;

public class ServerService implements Runnable {
    private final Socket nextClient;

    public ServerService(Socket nextClient) {
        this.nextClient = nextClient;
    }

    @Override
    public void run() {
        try {
        	System.out.println("Connected to client: " +
                    nextClient.getInetAddress() + ":" + nextClient.getPort());            
            BufferedReader reader = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));
            PrintWriter pw = new PrintWriter(nextClient.getOutputStream(), true);

            String command;
            while  (!nextClient.isClosed() && (command = reader.readLine()) != null) {
                Server.logClientRequest(nextClient.getInetAddress() + ":" + nextClient.getPort(), command);
                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    break;
                }
                
                if (command.equalsIgnoreCase("SYSINFO")) {
                    File outputFile = new File("system_info.txt");
                    ProcessBuilder processBuilder = new ProcessBuilder("./system.sh");
                    processBuilder.redirectOutput(outputFile);
                    Process process = processBuilder.start();
                    process.waitFor();
                    
                    long fileSize = outputFile.length();
                    pw.println(outputFile.getName() + ":" + fileSize);
                                        
                    try (BufferedInputStream fromFile = new BufferedInputStream(new FileInputStream(outputFile));
                        OutputStream toClient = nextClient.getOutputStream()) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fromFile.read(buffer)) != -1) {
                            toClient.write(buffer, 0, bytesRead);
                        }
                        toClient.flush();
                    }
                } else {
                    pw.println(command + ": command not found");
                }
                Server.displayClientRequests();
            }
            nextClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
