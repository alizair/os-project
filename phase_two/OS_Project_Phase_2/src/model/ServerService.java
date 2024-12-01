package model;

import java.io.*;
import java.net.Socket;

public class ServerService implements Runnable {
    private final Socket nextClient;
    private static final Object sysInfoLock = new Object();

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

            while (!nextClient.isClosed() && (command = reader.readLine()) != null) {
                Server.logClientRequest(nextClient.getInetAddress() + ":" + nextClient.getPort(), command);

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    break;
                }

                if (command.equalsIgnoreCase("SYSINFO")) {
                    synchronized (sysInfoLock) {
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
                                if (!nextClient.isClosed()) {
                                    toClient.write(buffer, 0, bytesRead);
                                }
                            }
                            toClient.flush();
                        }
                    }
                } else {
                    pw.println(command + ": command not found");
                }

                Server.displayClientRequests();

                System.out.println("Waiting for 5 minutes before processing the next command...");
                Thread.sleep(5*60*1000);
            }
            nextClient.close();
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Delay interrupted: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
