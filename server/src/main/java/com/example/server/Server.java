package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public final class Server {
    static final Logger logger = Logger.getLogger(Server.class.getName());

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final List<ClientHandler> activeConnections = new CopyOnWriteArrayList<>();

    private static Server instance;

    private Server(ServerSocket serverSocket, ExecutorService executorService) {
        this.serverSocket = serverSocket;
        this.executorService = executorService;
    }

    public static synchronized Server create(int port, int maxConnections, int queueSize, int keepAliveTime) throws IOException {
        if (port <= 0 || maxConnections <= 0 || queueSize <= 0 || keepAliveTime <= 0) {
            logger.severe("[SERVER] Invalid argument value");
            throw new IllegalArgumentException("Invalid argument value");
        }

        ServerSocket serverSocket = new ServerSocket(port);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueSize);
        ExecutorService executorService = new ThreadPoolExecutor(maxConnections, maxConnections, keepAliveTime, TimeUnit.MILLISECONDS, queue);

        return new Server(serverSocket, executorService);
    }

    public static synchronized Server getInstance(ServerConfig config) throws IOException {
        if (instance == null) {
            instance = create(config.getPort(), config.getMaxConnections(), config.getQueueSize(), config.getKeepAliveTime());
        }
        return instance;
    }

    public void start() throws IOException {
        while (true) {
            try {
                logger.info("[SERVER] Waiting for client connection...");

                Socket socket = serverSocket.accept();

                ClientHandler clientConnection = new ClientHandler(socket, this);
                addClient(clientConnection);

                executorService.submit(clientConnection);

                logger.info("[SERVER] Client connected " + socket.getInetAddress() + " Active threads: " + ((ThreadPoolExecutor) executorService).getActiveCount());

                String message = "[SERVER] " + clientConnection.getName() + " successfully connected.";
                broadcastMessage(message);
            } catch (IOException e) {
                logger.severe("[SERVER] Error accepting client connection: " + e.getMessage());
            }
        }
    }

    public void broadcastMessage(String message) {
        logger.info("[SERVER] Broadcasting message...");
        activeConnections.forEach(clientConnection -> clientConnection.sendMessage(message));
    }

    private void addClient(ClientHandler clientHandler) {
        logger.info("[SERVER] Adding client...");
        activeConnections.add(clientHandler);
        broadcastMessage(String.format("[SERVER] %s has connected.", clientHandler.getName()));
    }

    public void removeClient(ClientHandler clientHandler) {
        logger.info("[SERVER] Removing client...");
        activeConnections.remove(clientHandler);
        broadcastMessage(String.format("[SERVER] %s has disconnected.", clientHandler.getName()));
    }

    public void stop() throws IOException {
        logger.info("[SERVER] Stopping server...");
        executorService.shutdown();
        serverSocket.close();
        logger.info("[SERVER] Server stopped.");
    }
}
