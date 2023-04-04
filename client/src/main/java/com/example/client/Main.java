package com.example.client;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

public final class Main {
    public static void main(String[] args) throws ParseException {
        CommandLine cmd = parseArguments(args);

        ClientConfig clientConfig = ClientConfig.create(
                cmd.getOptionValue("h", "localhost"),
                Integer.parseInt(cmd.getOptionValue("p", "8080"))
        );

        System.out.printf("[Client.Main] Client connecting to %s:%d%n", clientConfig.getIpAddress(), clientConfig.getPort());

        try {
            Client client = Client.create(clientConfig.getIpAddress(), clientConfig.getPort());

            System.out.print("Enter file path or 'exit' to exit: ");
            String filePath = cmd.getOptionValue("f");

            if (filePath.equals("exit")) {
                client.sendMessage("-exit");
                client.stop();
            } else {
                File file = new File(filePath);

                if (!file.exists() || !file.isFile()) {
                    System.err.printf("[Client.Main] File %s does not exist or is not a file%n", filePath);
                }

                client.sendFile(file);
            }

            System.out.println("[Client.Main] Client stopped");
        } catch (IOException e) {
            System.out.println("[Client.Main] Error starting client: " + e.getMessage());
        }
    }


    private static CommandLine parseArguments(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", "host", true, "server hostname");
        options.addOption("p", "port", true, "server port number");
        options.addOption("f", "file", true, "file to send to server");

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
    }
}
