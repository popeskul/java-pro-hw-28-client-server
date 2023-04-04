package com.example.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.client.Client.CLIENT_RECEIVED_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private Client client;

    @BeforeEach
    void setUp() {
        // создаем серверный сокет
        try (ServerSocket ss = new ServerSocket(0)) {
            int port = ss.getLocalPort();

            // запускаем сервер в отдельном потоке
            Thread serverThread = new Thread(() -> {
                try {
                    Socket clientSocket = ss.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String message = in.readLine(); // ждем сообщение от клиента
                    out.println(message); // отправляем ответ клиенту
                    in.close();
                    out.close();
                    clientSocket.close();
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();

            // создаем клиентский объект
            client = Client.create("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        System.out.println("Client closed");
        client.stop();
    }

    @Test
    void testStart() throws InterruptedException {
        final String message = "test message";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Thread clientThread = new Thread(() -> {
            try {
                client.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
        client.sendMessage(message);
        clientThread.join();

        assertEquals(String.format(CLIENT_RECEIVED_MESSAGE, message), outContent.toString().trim());
    }

    @Test
    void testCreate() throws IOException {
        assertNotNull(client);

        // проверяем, что сокет создан
        assertNotNull(client.getSocket());
        assertTrue(client.getSocket().isConnected());

        // проверяем, что потоки созданы
        assertNotNull(client.getSocket().getInputStream());
        assertNotNull(client.getSocket().getOutputStream());

        // проверяем, что потоки доступны для чтения и записи
        assertEquals(0, client.getSocket().getInputStream().available());

        // проверяем, что сокет закрыт
        client.getSocket().close();
        assertTrue(client.getSocket().isClosed());

        // проверяем, что потоки закрыты
        assertThrows(IOException.class, () -> client.getSocket().getInputStream().read());
    }

    @Test
    void testSendMessage() throws InterruptedException {
        // создаем серверный сокет
        try (ServerSocket ss = new ServerSocket(0)) {
            int port = ss.getLocalPort();
            String message = "test message";

            // запускаем сервер в отдельном потоке
            Thread serverThread = new Thread(() -> Server(ss));
            serverThread.start();

            // создаем клиентский объект и отправляем сообщение
            Client client = Client.create("localhost", port);
            client.sendMessage(message);

            // ждем получения ответа от сервера
            Thread.sleep(500);

            // проверяем, что клиент получил ответ от сервера
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            client.start();
            String expectedOutput = String.format(Client.CLIENT_RECEIVED_MESSAGE + "%n", "Server received message: " + message);
            assertEquals(expectedOutput, outContent.toString());

            // останавливаем клиентский объект
            client.stop();

            // останавливаем серверный объект
            serverThread.join();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendFile() throws Exception {
        // создаем серверный сокет
        try (ServerSocket ss = new ServerSocket(0)) {
            Client client = Client.create("localhost", ss.getLocalPort());
            String message = "-f test.txt:asdasd";

            // запускаем сервер в отдельном потоке
            Thread serverThread = new Thread(() -> Server(ss));
            serverThread.start();

            // отправляем сообщение
            client.sendMessage(message);

            // ждем получения ответа от сервера
            Thread.sleep(500);

            // проверяем, что клиент получил ответ от сервера
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            client.start();
            String expectedOutput = String.format(Client.CLIENT_RECEIVED_MESSAGE + "%n", "Server received message: " + message);
            assertEquals(expectedOutput, outContent.toString());

            // останавливаем клиентский объект
            client.stop();

            // останавливаем серверный объект
            serverThread.join();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Server(ServerSocket ss) {
        try {
            Socket clientSocket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String receivedMessage = in.readLine();
            out.println("Server received message: " + receivedMessage);
            in.close();
            out.close();
            clientSocket.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
