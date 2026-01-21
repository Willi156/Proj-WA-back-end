package com.critiverse.controller;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiverse.dao.UtenteDao;
import com.critiverse.model.Utente;
import com.critiverse.service.EmailService;

@RestController
@RequestMapping("/api")
public class UtenteController {

    private final UtenteDao utenteDao;
    private final EmailService emailService;

    public UtenteController(UtenteDao utenteDao, EmailService emailService) {
        this.utenteDao = utenteDao;
        this.emailService = emailService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body(Map.of("serverTime", Instant.now().toString()));
    }

    @GetMapping("/utente/first")
    public ResponseEntity<?> firstUtente() {
        try{
        Optional<Utente> maybe = utenteDao.findFirst();
        if (maybe.isPresent()) {
            return ResponseEntity.ok(maybe.get());
        }
        return ResponseEntity.status(404).body(Map.of("message", "Nessun utente trovato"));
        } catch (Exception e){
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Errore interno del server"));
        }
    }

    @GetMapping("/utente/checkUsernameExists")
    public ResponseEntity<?> checkUsernameExists(@org.springframework.web.bind.annotation.RequestParam(name = "username", required = true) String username) {
        try{
        Optional<Boolean> maybe = utenteDao.checkUsernameExists(username);
        if (maybe.isPresent()) {
            return ResponseEntity.ok(maybe.get());
        }
        return ResponseEntity.status(404).body(Map.of("message", "Nessun utente trovato"));
        } catch (Exception e){
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Errore interno del server"));
        }
    }

    @PostMapping("/newUtente")
    public ResponseEntity<?> createUser(@RequestBody Utente req) {
        try {
                Optional<Utente> created = utenteDao.newUtente(
                    req.getNome(),
                    req.getCognome(),
                    req.getEmail(),
                    req.getUsername(),
                    req.getPassword()
                );
            return created.<ResponseEntity<?>>map(c -> {
                try {
                    // Invia email di conferma in modo asincrono (non blocca la risposta)
                    emailService.sendRegistrationEmail(c.getEmail(), c.getUsername());
                    return ResponseEntity.created(new URI("/api/newUtente/" + c.getId())).body(c);
                } catch (java.net.URISyntaxException e) {
                    return ResponseEntity.status(500).body(Map.of("message", "Failed to build resource URI"));
                }
            }).orElseGet(() -> ResponseEntity.status(500).body(Map.of("message", "Failed to create utente")));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }


}
