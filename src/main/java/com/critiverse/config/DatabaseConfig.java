package com.critiverse.config;

import java.net.URI;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        try {
            if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
                log.info("Detected DATABASE_URL environment variable, configuring DataSource from it");
                URI dbUri = new URI(databaseUrl);
                String userInfo = dbUri.getUserInfo();
                String username = null;
                String password = null;
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }
                String host = dbUri.getHost();
                int port = dbUri.getPort();
                String path = dbUri.getPath(); // includes leading '/'
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s?sslmode=require", host, port, path);

                DataSourceBuilder<?> builder = DataSourceBuilder.create();
                builder.driverClassName(env.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver"));
                builder.url(jdbcUrl);
                if (username != null) builder.username(username);
                if (password != null) builder.password(password);
                return builder.build();
            }
        } catch (Exception ex) {
            log.error("Error parsing DATABASE_URL, falling back to application properties", ex);
        }

        // Fallback to properties (local dev)
        String url = env.getProperty("spring.datasource.url");
        String user = env.getProperty("spring.datasource.username");
        String pass = env.getProperty("spring.datasource.password");
        String driver = env.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.driverClassName(driver);
        builder.url(url);
        if (user != null) builder.username(user);
        if (pass != null) builder.password(pass);
        return builder.build();
    }
}
