package com.example.server;

import java.io.IOException;

import org.apache.commons.cli.*;

public final class Main {
    public static void main(String[] args) {
        try {
            ServerConfig config = parseCommandLineArguments(args);
            Server server = Server.getInstance(config);
            server.start();
        } catch (ParseException e) {
            System.err.println("[Main.Server] Error parsing command line arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", new Options());
        } catch (IOException e) {
            System.err.println("[Main.Server] Error starting server: " + e.getMessage());
        }
    }

    private static ServerConfig parseCommandLineArguments(String[] args) throws ParseException {
        ServerConfig.ServerConfigBuilder configBuilder = ServerConfig.newBuilder();

        Options options = new Options();
        options.addOption("p", "port", true, "port number (default: 8080)");
        options.addOption("c", "max-connections", true, "maximum number of connections (default: 10)");
        options.addOption("q", "queue-size", true, "queue size (default: 10)");
        options.addOption("t", "keep-alive-time", true, "keep alive time in milliseconds (default: 5000)");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        configBuilder.withPort(Integer.parseInt(cmd.getOptionValue("p", "8080")));
        configBuilder.withMaxConnections(Integer.parseInt(cmd.getOptionValue("c", "10")));
        configBuilder.withQueueSize(Integer.parseInt(cmd.getOptionValue("q", "10")));
        configBuilder.withKeepAliveTime(Integer.parseInt(cmd.getOptionValue("t", "5000")));

        return configBuilder.build();
    }
}
