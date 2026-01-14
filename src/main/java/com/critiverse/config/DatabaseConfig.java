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
            if (databaseUrl != null && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
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
                // Log only host/port/db, not full URL or credentials
                log.info("Detected DATABASE_URL, configuring DataSource for host={} port={} db={}", host, port, path);
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
        // Normalize URL: if it's missing the jdbc: prefix but uses postgresql:// or postgres://, add jdbc:
        if (url != null && !url.startsWith("jdbc:")) {
            if (url.startsWith("postgresql://") || url.startsWith("postgres://")) {
                if (url.contains("?")) {
                    url = "jdbc:" + url;
                } else {
                    url = "jdbc:" + url + "?sslmode=require";
                }
                // parse minimal info for logging
                try {
                    URI u = new URI(url.substring(5)); // strip leading jdbc:
                    log.info("Normalized spring.datasource.url for host={} port={} db={}", u.getHost(), u.getPort(), u.getPath());
                } catch (Exception ignore) {
                    log.info("Normalized spring.datasource.url to jdbc form (details omitted)");
                }
            }
        }
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.driverClassName(driver);
        builder.url(url);
        if (user != null) builder.username(user);
        if (pass != null) builder.password(pass);
        return builder.build();
    }
}
