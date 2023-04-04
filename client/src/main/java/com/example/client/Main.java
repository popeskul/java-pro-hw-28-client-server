package com.example.client;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        ClientConfig clientConfig = parseArguments(args).orElse(ClientConfig.defaultConfig());

        System.out.printf("[Client.Main] Client connecting to %s:%d%n", clientConfig.getIpAddress(), clientConfig.getPort());

        try {
            Client client = Client.create(clientConfig.getIpAddress(), clientConfig.getPort());

            Scanner scanner = new Scanner(System.in);
            String filePath;

            System.out.print("Enter file path or 'exit' to exit: ");
            filePath = scanner.nextLine();

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


    private static Optional<ClientConfig> parseArguments(String[] args) {
        Options options = new Options();
        options.addOption("h", "host", true, "server hostname");
        options.addOption("p", "port", true, "server port number");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            String ipAddress = cmd.getOptionValue("h", "localhost");
            int port = Integer.parseInt(cmd.getOptionValue("p", "8080"));

            return Optional.of(ClientConfig.create(ipAddress, port));
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            printUsage(options);
            return Optional.empty();
        }
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Client", options);
    }
}
