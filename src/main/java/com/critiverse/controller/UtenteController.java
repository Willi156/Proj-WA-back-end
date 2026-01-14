package com.critiverse.controller;

import com.critiverse.dao.UtenteDao;
import com.critiverse.model.Utente;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class UtenteController {

    private final UtenteDao utenteDao;

    public UtenteController(UtenteDao utenteDao) {
        this.utenteDao = utenteDao;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body(java.util.Map.of("serverTime", Instant.now().toString()));
    }

    @GetMapping("/utente/first")
    public ResponseEntity<?> firstUtente() {
        java.util.Optional<Utente> maybe = utenteDao.findFirst();
        if (maybe.isPresent()) {
            return ResponseEntity.ok(maybe.get());
        }
        return ResponseEntity.status(404).body(java.util.Map.of("message", "Nessun utente trovato"));
    }
}
