package model;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {

    private static final String LOG_FILE_PATH = "./process_info.log";

    public static void main(String[] args) {

        Socket client = null;
        BufferedReader fromServer = null;
        PrintWriter toServer = null;
        String serverInput;

        try {
            runScript("search.sh");
            runScript("clientinfo.sh");

            System.out.println("Contents of " + LOG_FILE_PATH + ":");
            printLogFileContents(LOG_FILE_PATH);

            client = new Socket("10.10.10.131", 1300);
            System.out.println("Connected with server " + client.getInetAddress() + ":" + client.getPort());

            fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
            toServer = new PrintWriter(client.getOutputStream(), true);

            Scanner sc = new Scanner(System.in);
            String command;

            do {
                toServer.println("Client Request System Information");
                toServer.flush();

                StringBuilder systemInfo = new StringBuilder();
                while ((serverInput = fromServer.readLine()) != null && !serverInput.isEmpty()) {
                    systemInfo.append(serverInput).append("\n");
                }

                System.out.println("System Information from Server:");
                System.out.println(systemInfo.toString());

                System.out.print("Enter a command: ");
                command = sc.nextLine();
                toServer.println(command);
                toServer.flush();

                if (!command.equalsIgnoreCase("exit")) {
                    try {
                        String serverResponse = fromServer.readLine();
                        if (serverResponse != null) {
                            System.out.println("Server response: " + serverResponse);

                            if (!serverResponse.contains("command not found") && serverResponse.contains(":")) {
                                String fileName = serverResponse.split(":")[0];
                                File receivedFile = new File(fileName);

                                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(receivedFile));
                                     InputStream is = client.getInputStream()) {

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

                System.out.println("Waiting for 5 seconds before sending the next command...");
                Thread.sleep(5000);

            } while (!command.equalsIgnoreCase("exit"));

        } catch (IOException e) {
            System.out.println("Error: " + e);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                if (fromServer != null)
                    fromServer.close();
                if (toServer != null)
                    toServer.close();
                if (client != null)
                    client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private static void runScript(String scriptName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", scriptName);
            pb.directory(new File("./"));
            Process process = pb.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            System.out.println("Output of " + scriptName + ":");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Script " + scriptName + " exited with error code " + exitCode);
            }
        } catch (IOException e) {
            System.out.println("Error executing script " + scriptName + ": " + e);
        } catch (Exception e) {
            System.out.println("Error executing script " + scriptName + ": " + e);
        }
    }

    private static void printLogFileContents(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Log file not found: " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading log file: " + e.getMessage());
        }
    }
}

