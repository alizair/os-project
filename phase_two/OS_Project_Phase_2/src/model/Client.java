package model;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        try {
            Socket service = new Socket("10.10.10.131", 1300);
            System.out.println("Connected with server " +
                    service.getInetAddress() + ":" +
                    service.getPort());

            service.setSoTimeout(305000);

            PrintWriter output = new PrintWriter(service.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(service.getInputStream()));
            Scanner sc = new Scanner(System.in);
            String command;

            do {
                System.out.print("Enter a command: ");
                command = sc.nextLine();
                output.println(command);
                output.flush();

                if (!command.equalsIgnoreCase("exit")) {
                    try {
                        String serverResponse = reader.readLine();
                        if (serverResponse != null) {
                            System.out.println("Server response: " + serverResponse);

                            // Check if the server sent a file
                            if (!serverResponse.contains("command not found") && serverResponse.contains(":")) {

                                String fileName = serverResponse.split(":")[0];
                                File receivedFile = new File(fileName);

                                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(receivedFile));
                                     InputStream is = service.getInputStream()) {

                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = is.read(buffer)) != -1) {
                                        bos.write(buffer, 0, bytesRead);
                                    }

                                    System.out.println("File received and saved as: " + receivedFile.getName());
                                }

                                try (BufferedReader fileReader = new BufferedReader(new FileReader(receivedFile))) {
                                    System.out.println("File content:");
                                    String line;
                                    while ((line = fileReader.readLine()) != null) {
                                        System.out.println(line);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error receiving data: " + e.getMessage());
                    }
                }

                System.out.println("Waiting for 5 minutes before sending the next command...");
                Thread.sleep(5*60*1000);

            } while (!command.equalsIgnoreCase("exit"));

            service.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
