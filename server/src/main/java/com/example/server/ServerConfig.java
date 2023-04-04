package com.example.server;

public final class ServerConfig {
    private final int port;
    private final int maxConnections;
    private final int queueSize;
    private final int keepAliveTime;

    private ServerConfig(ServerConfigBuilder serverConfigBuilder) {
        this.port = serverConfigBuilder.port;
        this.maxConnections = serverConfigBuilder.maxConnections;
        this.queueSize = serverConfigBuilder.queueSize;
        this.keepAliveTime = serverConfigBuilder.keepAliveTime;
    }

    public int getPort() {
        return port;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public static ServerConfigBuilder newBuilder() {
        return new ServerConfigBuilder();
    }

    public static class ServerConfigBuilder {
        private int port = 8080;
        private int maxConnections = 10;
        private int queueSize = 10;
        private int keepAliveTime = 5000;

        public ServerConfigBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public ServerConfigBuilder withMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public ServerConfigBuilder withQueueSize(int queueSize) {
            this.queueSize = queueSize;
            return this;
        }

        public ServerConfigBuilder withKeepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public ServerConfig build() {
            return new ServerConfig(this);
        }
    }
}
