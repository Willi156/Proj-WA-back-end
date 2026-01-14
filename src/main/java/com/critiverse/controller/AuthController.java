package com.critiverse.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiverse.dao.UtenteDao;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UtenteDao utenteDao;

    public AuthController(UtenteDao utenteDao) {
        this.utenteDao = utenteDao;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            return utenteDao.findByUsernameAndPassword(req.getUsername(), req.getPassword())
                    .<ResponseEntity<?>>map(u -> {
                        // hide password before returning
                        u.setPassword(null);
                        return ResponseEntity.ok(u);
                    })
                    .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
        } catch (Exception ex) {
            log.error("Error during login", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
