package com.example.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientConfigTest {
    @Test
    void testCreate() {
        ClientConfig config = ClientConfig.create("127.0.0.1", 8000);
        assertEquals("127.0.0.1", config.getIpAddress());
        assertEquals(8000, config.getPort());
    }

    @Test
    void testDefaultConfig() {
        ClientConfig config = ClientConfig.defaultConfig();
        assertEquals("localhost", config.getIpAddress());
        assertEquals(8080, config.getPort());
    }
}