package com.novelverse.novelverse.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/novel_db}")
    private String url;

    @Value("${spring.datasource.username:admin}")
    private String username;

    @Value("${spring.datasource.password:1234}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
}
