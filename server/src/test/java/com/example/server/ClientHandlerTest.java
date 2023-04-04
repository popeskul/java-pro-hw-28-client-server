package com.example.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientHandlerTest {
    @Test
    void testCreateClientHandler() throws IOException {
        System.out.println("testCreateClientHandler");
        Server server = Server.create(8081, 10, 10, 10);
        Socket socket = new Socket("localhost", 8081);
        ClientHandler handler = new ClientHandler(socket, server);

        assertNotNull(handler);
        assertNotNull(handler.getName());
        assertNotNull(handler.getServer());

        socket.close();
        server.stop();
    }

    @Test
    void testSendMessage() throws IOException {
        Server server = Server.create(8082, 10, 10, 10);
        Socket socket = new Socket("localhost", 8082);
        ClientHandler handler = new ClientHandler(socket, server);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Отправляем сообщение от клиента
        String message = "Test message";
        handler.sendMessage(message);

        // Сравниваем ожидаемый вывод с фактическим
        System.out.print(message);
        assertEquals(message, outContent.toString());
    }

    @Test
    public void testReceiveFileFromServer() throws IOException {
        Server server = Server.create(8083, 10, 10, 10);
        Socket socket = new Socket("localhost", 8083);
        ClientHandler handler = new ClientHandler(socket, server);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String message = "-file test.txt:hello world";
        handler.sendMessage(message);

        String expectedOutput = "hello world";
        System.out.print(expectedOutput);
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testReceiveFile(@TempDir Path tempDir) throws IOException {
        Server server = Server.create(8084, 10, 10, 10);
        Socket socket = new Socket("localhost", 8084);

        String fileName = "test-file.txt";
        String fileContent = "test content";
        String message = "-file " + fileName + ":" + fileContent;

        ClientHandler clientHandler = new ClientHandler(socket, server);
        Path testFilePath = Files.write(tempDir.resolve(fileName), fileContent.getBytes());
        Path p = Files.write(testFilePath, "test content".getBytes());

        clientHandler.receiveFile(message);

        Path savedFilePath = tempDir.resolve(fileName);

        assertTrue(Files.exists(savedFilePath));
        try {
            String savedFileContent = new String(Files.readAllBytes(savedFilePath));
            assertEquals(fileContent, savedFileContent);
        } catch (IOException e) {
            fail("Error reading saved file: " + e.getMessage());
        }

        boolean isDeleted = savedFilePath.toFile().delete();
        assertTrue(isDeleted);

        server.stop();
        socket.close();
    }
}