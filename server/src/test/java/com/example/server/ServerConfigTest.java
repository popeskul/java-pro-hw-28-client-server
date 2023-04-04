package com.example.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigTest {
    @Test
    void testCreateBuilder() {
        ServerConfig.ServerConfigBuilder builder = ServerConfig.newBuilder();
        assertNotNull(builder);
    }

    @Test
    void testBuilderSetters() {
        ServerConfig.ServerConfigBuilder builder = ServerConfig.newBuilder();
        builder.withPort(1234);
        builder.withMaxConnections(20);
        builder.withQueueSize(5);
        builder.withKeepAliveTime(10000);
        ServerConfig config = builder.build();

        assertEquals(1234, config.getPort());
        assertEquals(20, config.getMaxConnections());
        assertEquals(5, config.getQueueSize());
        assertEquals(10000, config.getKeepAliveTime());
    }

    @Test
    void testBuild() {
        ServerConfig.ServerConfigBuilder builder = ServerConfig.newBuilder();
        builder.withPort(1234);
        builder.withMaxConnections(20);
        builder.withQueueSize(5);
        builder.withKeepAliveTime(10000);
        ServerConfig config = builder.build();

        assertNotNull(config);
        assertEquals(1234, config.getPort());
        assertEquals(20, config.getMaxConnections());
        assertEquals(5, config.getQueueSize());
        assertEquals(10000, config.getKeepAliveTime());
    }
}