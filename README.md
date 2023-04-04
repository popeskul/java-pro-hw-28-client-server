# Java Client-Server Project

This is a multi-module project in Java that consists of a client and a server. The project is fully covered with tests to ensure its reliability and efficiency.

## Features

The client in this project can send a file to the server by making a request using the following format:

```bash
-f file-name.txt:long text
```

## Technologies

This project is implemented in Java using the following technologies:
- Maven
- JUnit 5
- Mockito

## How to works
### Server

Optional params: -p, -c, -q, -t
```bash
java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar -p 8080 -c 10 -q 10 -t 5000
```

### Client

Optional params: -p, -h, -t
```bash
java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar -f file.txt -p 8080 -h localhost
```


