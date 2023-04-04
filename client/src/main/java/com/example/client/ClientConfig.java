package com.example.client;

public final class ClientConfig {
    private final String ipAddress;
    private final int port;

    private ClientConfig(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public static ClientConfig create(String ipAddress, int port) {
        return new ClientConfig(ipAddress, port);
    }

    public static ClientConfig defaultConfig() {
        return new ClientConfig("localhost", 8080);
    }
}