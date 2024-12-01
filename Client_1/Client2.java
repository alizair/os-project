package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client2 {

    public static void main(String[] args) {

        Socket client = null;
        BufferedReader from_server = null;
        PrintWriter to_server = null;

        String serverInput;

        try {

            runScript("search.sh");
            runScript("clientinfo.sh");

            // connected with the server
            client = new Socket("localhost", 1300);
            System.out.println("Connected with server " + client.getInetAddress() + ":"
                    + client.getPort());

            from_server = new BufferedReader(new InputStreamReader(client.getInputStream()));
            to_server = new PrintWriter(client.getOutputStream(), true);

            for(;;) {
                // send request to server
                to_server.println("Client Request System Information");
                to_server.flush();

                //it will read response from server
                StringBuilder systemInfo = new StringBuilder();
                // it will read tell the end 
                while ((serverInput = from_server.readLine()) != null && !serverInput.isEmpty()) {
                    systemInfo.append(serverInput).append("\n");
                }

                // Display the received system info
                System.out.println("System Information from Server:");
                System.out.println(systemInfo.toString());

                // thread will wait for 5 minute before next request
                Thread.sleep(5 * 60 * 1000);
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                if (from_server != null)
                    from_server.close();
                if (to_server != null)
                    to_server.close();
                if (client != null)
                    client.close();
            } catch (IOException ioee) {
                System.err.println(ioee);
            }
        }
    }

    // this function to run a shell script and display its output
    private static void runScript(String scriptName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", scriptName);
            Process process = pb.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            System.out.println("Output of " + scriptName + ":");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing script " + scriptName + ": " + e);
        }
    }
}
