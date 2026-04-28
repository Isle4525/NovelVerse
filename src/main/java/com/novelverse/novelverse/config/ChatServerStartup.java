package com.novelverse.novelverse.config;

import com.novelverse.novelverse.socket.ChatServer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class ChatServerStartup {

    @Bean
    public ApplicationRunner chatServerRunner(ChatServer chatServer, ExecutorService executorService) {
        return args -> executorService.submit(chatServer::start);
    }
}
