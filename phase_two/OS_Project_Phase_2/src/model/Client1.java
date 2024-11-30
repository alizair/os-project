package model;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {
    public static void main(String[] args) {
        try {
            Socket service = new Socket("10.10.10.131", 1300);
            System.out.println("Connected to server: " +
                    service.getInetAddress() + ":" + service.getPort());

            PrintWriter output = new PrintWriter(service.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(service.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            String command;
            while (true) {
                System.out.print("Enter a command: ");
                command = scanner.nextLine();
                output.println(command);

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exited");
                    break;
                }

                String serverResponse = reader.readLine();
                if (serverResponse == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }
                
                if (!serverResponse.contains("command not found")) {

                    String[] response = serverResponse.split(":");
                    String fileName = response[0];
                    long fileSize = Long.parseLong(response[1]);

                    File receivedFile = new File(fileName);
                    System.out.println("Receiving file: " + receivedFile.getName());
                    
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(receivedFile));
                         InputStream is = service.getInputStream()) {
                        byte[] buffer = new byte[4096];
                        long totalBytesRead = 0;
                        int bytesRead;
                        while (totalBytesRead < fileSize && (bytesRead = is.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                        }
                        System.out.println("File received and saved as: " + receivedFile.getName());
                    }

                    try (BufferedReader fileReader = new BufferedReader(new FileReader(receivedFile))) {
                        String line;
                        System.out.println("File content:");
                        while ((line = fileReader.readLine()) != null) {
                            System.out.println(line);
                        }
                    }
                } else {
                    System.out.println("Server response: " + serverResponse);
                }
            }
            scanner.close();
            service.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
