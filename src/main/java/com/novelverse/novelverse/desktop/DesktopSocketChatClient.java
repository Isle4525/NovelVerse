package com.novelverse.novelverse.desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class DesktopSocketChatClient {

    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Thread listenerThread;

    public DesktopSocketChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect(String username, Consumer<String> onMessage) throws IOException {
        disconnect();

        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        writer.println("/name " + username);

        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    onMessage.accept(line);
                }
            } catch (IOException ignored) {
            }
        }, "desktop-chat-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public synchronized void send(String text) {
        if (writer == null) {
            throw new IllegalStateException("Chat is not connected");
        }
        writer.println(text);
    }

    public synchronized void disconnect() {
        if (listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }
        if (writer != null) {
            writer.close();
            writer = null;
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ignored) {
        } finally {
            reader = null;
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        } finally {
            socket = null;
        }
    }
}
