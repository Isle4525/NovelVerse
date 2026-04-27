package com.novelverse.novelverse.config;

import com.novelverse.novelverse.repository.BookmarkRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Bean
    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(4);
    }
}
