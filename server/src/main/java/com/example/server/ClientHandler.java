package com.example.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

public final class ClientHandler implements Runnable {
    Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static final String FILE_PREFIX = "-file ";

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String name;
    private final Server server;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.name = "client-" + UUID.randomUUID().toString().substring(0, 4);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public Server getServer() {
        return server;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                logger.info(String.format("[%s] Received message: %s", this.name, message));

                if (message.equals("-exit")) {
                    break;
                } else if (message.startsWith(FILE_PREFIX)) {
                    receiveFile(message);
                } else {
                    String response = String.format("[%s] %s", this.name, message);
                    server.broadcastMessage(response);
                }
            }
        } catch (IOException e) {
            logger.severe(String.format("[%s] Error reading/writing data: %s", this.name, e.getMessage()));
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                    logger.info(String.format("[%s] Client disconnected %s", this.name, socket.getInetAddress()));
                }

                server.removeClient(this);
            } catch (IOException e) {
                logger.severe(String.format("[%s] Error closing client socket: %s", this.name, e.getMessage()));
            }
        }
    }

    void receiveFile(String message) throws IOException {
        logger.info(String.format("[%s] Receiving: %s", this.name, message));

        String fileName = message.substring(FILE_PREFIX.length(), message.indexOf(":"));
        String content = message.substring(message.indexOf(":") + 1);

        logger.info(String.format("[%s] content: %s", this.name, content));

        Path path = Paths.get("tmp/" + fileName);

        // проверяем, существует ли директория, и создаем ее, если не существует
        Path dir = path.getParent();
        if (!Files.exists(dir)) {
            try {
                dir = Files.createDirectories(dir);
            } catch (IOException e) {
                logger.severe(String.format("[%s] Error creating directory: %s", this.name, dir));
                return;
            }
        }

        try {
            Path p = Files.write(path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("File saved to: " + p.toFile().getAbsolutePath());
            logger.info(String.format("[%s] File saved to: %s", this.name, p));

            String fileContents = Arrays.toString(Files.readAllBytes(p));
            logger.info(String.format("[%s] File contents: %s", this.name, fileContents));
        } catch (IOException e) {
            logger.severe("[%s] Error saving data to file: %s" + this.name + " " + e.getMessage());
        }
    }
}
