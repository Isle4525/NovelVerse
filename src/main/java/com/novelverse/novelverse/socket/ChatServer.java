package com.novelverse.novelverse.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int PORT = 9090;

    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server started on port " + PORT);

            while (true){
                Socket socket = serverSocket.accept();

                ClientHandler client = new ClientHandler(socket,clients);
                clients.add(client);

                new Thread(client).start();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
