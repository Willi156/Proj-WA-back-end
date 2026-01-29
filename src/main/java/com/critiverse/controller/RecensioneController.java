package com.critiverse.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiverse.dao.RecensioneDao;
import com.critiverse.model.Recensione;


@RestController
@RequestMapping("/api")
public class RecensioneController {

    private static final Logger log = LoggerFactory.getLogger(RecensioneController.class);

    private final RecensioneDao recensioneDao;
    public RecensioneController(RecensioneDao recensioneDao) {
        this.recensioneDao = recensioneDao;
    }

    @GetMapping("/recensioni/contenuto")
    public ResponseEntity<?> getAllRecensioni(@org.springframework.web.bind.annotation.RequestParam(name = "contenutoId", required = true) Long contenutoId) {
        if (contenutoId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required query parameter 'contenutoId'"));
        }
        try {
            return ResponseEntity.ok(recensioneDao.findByContenutoId(contenutoId));
        } catch (Exception ex) {
            log.error("Error fetching recensioni for contenutoId={}", contenutoId, ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/recensioni/utente/{id}")
    public ResponseEntity<?> getRecensioniByUtente(@PathVariable("id") Long utenteId) {
        if (utenteId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        try {
            return ResponseEntity.ok(recensioneDao.findByUtenteIdWithContenuto(utenteId));
        } catch (Exception ex) {
            log.error("Error fetching recensioni for utenteId={}", utenteId, ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @DeleteMapping("/recensioni/delete/{id}")
    public ResponseEntity<?> deleteRecensione(@PathVariable("id") Long recensioneId) {
        if (recensioneId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        try {
            boolean deleted = recensioneDao.deleteById(recensioneId);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Recensione non trovata"));
            }
            return ResponseEntity.ok(Map.of("message", "Recensione eliminata"));
        } catch (Exception ex) {
            log.error("Error deleting recensione with id={}", recensioneId, ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/recensione/new")
    public ResponseEntity<?> createRecensione(@RequestBody(required = false) Recensione req) {
        if (req == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing request body"));
        }

        if (req.getTitolo() == null || req.getTesto() == null || req.getVoto() == null || req.getData() == null || req.getIdUtente() == null || req.getIdContenuto() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields: titolo, testo, voto, data, idUtente, idContenuto"));
        }

        try {
            var created = recensioneDao.createRecensione(
                req.getTitolo(),
                req.getTesto(),
                req.getVoto(),
                req.getData(),
                req.getIdUtente(),
                req.getIdContenuto()
            );

            if (created.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to create recensione"));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
        } catch (Exception ex) {
            log.error("Error creating recensione for idUtente={} idContenuto={}", req.getIdUtente(), req.getIdContenuto(), ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @PutMapping("/recensioni/update/{id}")
    public ResponseEntity<?> updateRecensione(
            @PathVariable("id") Long recensioneId,
            @RequestBody(required = false) Recensione req) {
        if (recensioneId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required path variable 'id'"));
        }
        if (req == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing request body"));
        }
        if (req.getTitolo() == null || req.getTesto() == null || req.getVoto() == null || req.getData() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields: titolo, testo, voto, data"));
        }

        try {
            boolean updated = recensioneDao.updateRecensione(
                recensioneId,
                req.getTitolo(),
                req.getTesto(),
                req.getVoto(),
                req.getData()
            );

            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Recensione non trovata"));
            }

            return ResponseEntity.ok(Map.of(
                "message", "Recensione aggiornata",
                "idRecensione", recensioneId,
                "titolo", req.getTitolo(),
                "testo", req.getTesto(),
                "voto", req.getVoto(),
                "data", req.getData()
            ));
        } catch (Exception ex) {
            log.error("Error updating recensione with id={}", recensioneId, ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/recensioni/latest")
    public ResponseEntity<?> getMostRecentRecensione() {
        try {
            Optional<Recensione> maybe = recensioneDao.findMostRecent();
            if (maybe.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Nessuna recensione trovata"));
            }
            return ResponseEntity.ok(maybe.get());
        } catch (Exception ex) {
            log.error("Error fetching most recent recensione", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }


}
