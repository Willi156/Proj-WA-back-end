package com.critiverse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private static final Logger log = LoggerFactory.getLogger(DebugController.class);

    private final JdbcTemplate jdbc;

    public DebugController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/db")
    public ResponseEntity<?> checkDb() {
        try {
            Integer v = jdbc.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok(Map.of("ok", true, "value", v));
        } catch (DataAccessException ex) {
            log.error("Database connectivity check failed", ex);
            return ResponseEntity.status(500).body(Map.of(
                    "ok", false,
                    "error", ex.getMessage()
            ));
        }
    }
}
