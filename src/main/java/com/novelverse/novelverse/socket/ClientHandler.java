package com.novelverse.novelverse.socket;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Set<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;
    private String username = "Guest-" + UUID.randomUUID().toString().substring(0, 8);

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            clients.add(this);
            broadcastSystem(username + " joined the chat");

            String message;

            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }

        } catch (IOException e) {
        } finally {
            clients.remove(this);
            broadcastSystem(username + " left the chat");
            closeQuietly(in);
            if (out != null) {
                out.close();
            }
            closeQuietly(socket);
        }
    }

    private void handleMessage(String message) {
        String trimmed = message == null ? "" : message.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        if (trimmed.startsWith("/name ")) {
            String newUsername = trimmed.substring(6).trim();
            if (!newUsername.isEmpty()) {
                String oldUsername = username;
                username = newUsername;
                broadcastSystem(oldUsername + " is now known as " + username);
            }
            return;
        }

        broadcast("[" + username + "] " + trimmed);
    }

    private void broadcast(String message){
        for(ClientHandler client: clients){
            if (client.out != null) {
                client.out.println(message);
            }
        }
    }

    private void broadcastSystem(String message) {
        broadcast("[System] " + message);
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

}
