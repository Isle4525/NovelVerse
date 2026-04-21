package com.novelverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NovelVerseApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelVerseApplication.class, args);
	}

}
