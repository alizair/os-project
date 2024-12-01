package client1;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Client1 {
	
	
    private static void runShellScript(String scriptPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            //read and display output from the script
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            //wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println(scriptPath + " exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error running script " + scriptPath + ": " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
	
	
    public static void main(String[] args) {
        //managing threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        System.out.println("Working directory: " + System.getProperty("user.dir"));


        //run login.sh
        Runnable loginTask = () -> {
            System.out.println("Running login.sh...");
            runShellScript("src/client1/login.sh");
        };

        //run check.sh
        Runnable checkTask = () -> {
            System.out.println("Running check.sh...");
            runShellScript("src/client1/check.sh");
        };

        //execute tasks
        executor.submit(loginTask);
        executor.submit(checkTask);

        // Shutdown the executor
        executor.shutdown();
    }


}
