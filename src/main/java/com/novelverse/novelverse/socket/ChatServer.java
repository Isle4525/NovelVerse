package com.novelverse.novelverse.socket;

import org.springframework.stereotype.Component;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ChatServer {

    private static final int PORT = 9090;

    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private final ExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public ChatServer(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void start(){
        if (!started.compareAndSet(false, true)) {
            return;
        }

        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            serverSocket.setReuseAddress(true);
            System.out.println("Server started on port " + PORT);

            while (true){
                Socket socket = serverSocket.accept();

                ClientHandler client = new ClientHandler(socket, clients);
                executorService.submit(client);
            }


        } catch (BindException e) {
            System.out.println("Chat server was not started because port " + PORT + " is already in use.");
            System.out.println("If this is another running NovelVerse chat instance, the desktop client can still connect to it.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            started.set(false);
        }
    }


}
