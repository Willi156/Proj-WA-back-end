package com.critiverse.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.critiverse.dao.UtenteDao;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final String SESSION_USER_ID = "USER_ID";

    private final UtenteDao utenteDao;

    public AuthController(UtenteDao utenteDao) {
        this.utenteDao = utenteDao;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        try {
            return utenteDao.findByUsernameAndPassword(req.getUsername(), req.getPassword())
                    .<ResponseEntity<?>>map(u -> {
                        // reset session to avoid fixation then store user id for subsequent requests
                        HttpSession existing = request.getSession(false);
                        if (existing != null) {
                            existing.invalidate();
                        }
                        HttpSession session = request.getSession(true);
                        session.setAttribute(SESSION_USER_ID, u.getId());

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        if (session == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (!(userId instanceof Long id)) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }

        return utenteDao.findById(id)
                .<ResponseEntity<?>>map(u -> {
                    u.setPassword(null);
                    return ResponseEntity.ok(u);
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Session user not found")));
    }
}
