package com.example.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public final class Client {
    Logger logger = Logger.getLogger(Client.class.getName());

    public final static String FILE_PREFIX = "-file ";
    public final static String CLIENT_RECEIVED_MESSAGE = "Client received message: %s";

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    private Client(Socket socket, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public static Client create(String ipAddress, int port) throws IOException {
        Socket socket = new Socket(ipAddress, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        return new Client(socket, in, out);
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendFile(final File file) {
        if (file == null) {
            logger.severe("File cannot be null");
            throw new IllegalArgumentException("File cannot be null");
        }

        String fileName = file.getName();
        logger.info("Client sending file: " + fileName);

        if (!file.exists() || !file.isFile()) {
            logger.warning("File does not exist or is not a file: " + fileName);
            return;
        }

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(FILE_PREFIX);
        requestBuilder.append(fileName);
        requestBuilder.append(":");

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                requestBuilder.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            logger.warning("Error sending file: " + e.getMessage());
            return;
        }

        sendMessage(requestBuilder.toString());
        logger.info("Client sent file: " + fileName);
    }

    public void sendMessage(Object message) {
        out.println(message);
    }

    public void start() throws IOException {
        Thread readerThread = new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.printf(CLIENT_RECEIVED_MESSAGE + "%n", message);
                } catch (IOException e) {
                    logger.warning("Error reading message: " + e.getMessage());
                    break;
                }
            }
        });
        readerThread.start();

        try {
            readerThread.join();
        } catch (InterruptedException e) {
            logger.warning("Thread interrupted: " + e.getMessage());
        }
    }

    public void stop() throws IOException {
        if (socket.isClosed()) {
            logger.warning("Socket is already closed");
            return;
        }

        in.close();
        out.close();
        socket.close();
    }
}
