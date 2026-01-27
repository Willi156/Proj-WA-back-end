package com.critiverse.controller;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiverse.dao.PreferitiDao;
import com.critiverse.dao.UtenteDao;
import com.critiverse.model.ContenutoSummary;
import com.critiverse.model.Utente;
import com.critiverse.service.EmailService;

@RestController
@RequestMapping("/api")
public class UtenteController {

    private static final Logger log = LoggerFactory.getLogger(UtenteController.class);

    private final UtenteDao utenteDao;
    private final EmailService emailService;
    private final PreferitiDao preferitiDao;

    public UtenteController(UtenteDao utenteDao, EmailService emailService, PreferitiDao preferitiDao) {
        this.utenteDao = utenteDao;
        this.emailService = emailService;
        this.preferitiDao = preferitiDao;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body(Map.of("serverTime", Instant.now().toString()));
    }

    // @GetMapping("/utente/{id}")
    // public ResponseEntity<?> getUtenteById(@PathVariable("id") Long id) {
    //     if (id == null) {
    //         return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
    //     }
    //     try {
    //         Optional<Utente> maybe = utenteDao.findById(id);
    //         if (maybe.isPresent()) {
    //             maybe = maybe.map(u -> {
    //                 u.setPassword(null); // Non espone la password
    //                 return u;
    //             });
    //             return ResponseEntity.ok(maybe.get());
    //         }
    //         return ResponseEntity.status(404).body(Map.of("message", "Utente non trovato"));
    //     } catch (Exception e) {
    //         log.error("Error fetching utente by id {}", id, e);
    //         return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
    //     }
    // }

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

    @GetMapping("/utente/{id}/preferiti")
    public ResponseEntity<?> getPreferiti(@PathVariable("id") Long idUtente) {
        if (idUtente == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        try {
            List<Long> preferiti = preferitiDao.findContenutoIdsByUtente(idUtente);
            return ResponseEntity.ok(preferiti);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
        }
    }

// inizio prova 
    @PutMapping("/utente/update/{id}")
    public ResponseEntity<?> updateUtente(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Utente req) {
        if (id == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        if (req == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing request body"));
        }
        if (req.getId() != null && !id.equals(req.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Path id and body id differ"));
        }
        try {
            Optional<Utente> maybeUpdated = utenteDao.updateUtenteInfo(
                id,
                req.getNome(),
                req.getCognome(),
                req.getEmail(),
                req.getImmagineProfilo()
            );
            if (maybeUpdated.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Utente non trovato"));
            }
            Utente updated = maybeUpdated.get();
            updated.setPassword(null); // Non esporre la password aggiornata
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating utente with id {}", id, e);
            return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
        }
    }

    @PutMapping("/utente/update/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, String> body) {
        if (id == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        if (body == null || !body.containsKey("password") || body.get("password") == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing password"));
        }
        String newPassword = body.get("password");
        try {
            boolean updated = utenteDao.updatePassword(id, newPassword);
            if (!updated) {
                return ResponseEntity.status(404).body(Map.of("message", "Utente non trovato"));
            }
            return ResponseEntity.ok(Map.of("message", "Password aggiornata"));
        } catch (Exception e) {
            log.error("Error updating password for utente with id {}", id, e);
            return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
        }
    }

    @PostMapping("/utente/{id}/checkPassword")
    public ResponseEntity<?> checkPassword(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, String> body) {
        if (id == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        if (body == null || !body.containsKey("password") || body.get("password") == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing password"));
        }
        String password = body.get("password");
        try {
            Optional<Boolean> maybeValid = utenteDao.verifyPassword(id, password);
            if (maybeValid.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Utente non trovato"));
            }
            return ResponseEntity.ok(maybeValid.get());
        } catch (Exception e) {
            log.error("Error checking password for utente with id {}", id, e);
            return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
        }
    }
    //FINE PROVA

    @GetMapping("/utente/{id}/preferitiCompleti")
    public ResponseEntity<?> getPreferitiCompleti(@PathVariable("id") Long idUtente) {
        if (idUtente == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        try {
            List<ContenutoSummary> preferiti = preferitiDao.findContenutiByUtente(idUtente);
            return ResponseEntity.ok(preferiti);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
        }
    }

    @PostMapping("/utente/{id}/addPreferito")
    public ResponseEntity<?> addPreferito(
            @PathVariable("id") Long idUtente,
            @org.springframework.web.bind.annotation.RequestBody(required = false) Map<String, Object> body) {
                Long idContenuto = null;
        if (body != null) {
            Object val = body.get("contenutoId");
            if (val instanceof Number number) {
                idContenuto = number.longValue();
            }
        }

        if (idUtente == null || idContenuto == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required parameters: id or contenutoId"));
        }
        try {
            boolean inserted = preferitiDao.addPreferito(idUtente, idContenuto);
            if (inserted) {
                return ResponseEntity.status(201).body(Map.of("message", "Preferito aggiunto"));
            }
            return ResponseEntity.status(200).body(Map.of("message", "Preferito gia' presente"));
        } catch (Exception e) {
            log.error("Error while adding preferito for idUtente={} idContenuto={}", idUtente, idContenuto, e);
            return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/utente/{id}/removePreferito")
    public ResponseEntity<?> deletePreferito(
            @PathVariable("id") Long idUtente,
            @org.springframework.web.bind.annotation.RequestBody(required = false) Map<String, Object> body) {
                Long idContenuto = null;
        if (body != null) {
            Object val = body.get("contenutoId");
            if (val instanceof Number number) {
                idContenuto = number.longValue();
            }
        }

        if (idUtente == null || idContenuto == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required parameters: id or contenutoId"));
        }
        try {
            Long preferitoId = preferitiDao.findPreferitoId(idUtente, idContenuto);
            if (preferitoId == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Preferito non trovato"));
            }
            boolean deleted = preferitiDao.deletePreferitoById(preferitoId);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Preferito rimosso"));
            }
            return ResponseEntity.status(500).body(Map.of("message", "Errore rimozione preferito"));
        } catch (Exception e) {
            log.error("Error while deleting preferito for idUtente={} idContenuto={}", idUtente, idContenuto, e);
            return ResponseEntity.status(500).body(Map.of("message", "Errore interno del server"));
        }
    } 

}
