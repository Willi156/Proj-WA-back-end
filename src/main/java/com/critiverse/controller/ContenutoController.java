package com.critiverse.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiverse.dao.ContenutoDao;
import com.critiverse.model.Contenuto;

@RestController
@RequestMapping("/api")
public class ContenutoController {

    private static final Logger log = LoggerFactory.getLogger(ContenutoController.class);

    private final ContenutoDao contenutoDao;

    public ContenutoController(ContenutoDao contenutoDao) {
        this.contenutoDao = contenutoDao;
    }

    @PostMapping("/newContenuto")
    public ResponseEntity<?> createContenuto(@RequestBody Contenuto req) {
        try {
            // Diagnostic log: inspect incoming payload mapping
            log.debug("Received new contenuto payload: titolo={}, descrizione={}, genere={}, link={}, tipo={}, annoPubblicazione={}, casaProduzione={}, casaEditrice={}, inCorso={}, stagioni={}, piattaformaIds={}",
                    req.getTitolo(), req.getDescrizione(), req.getGenere(), req.getLink(), req.getTipo(), req.getAnnoPubblicazione(),
                    req.getCasaProduzione(), req.getCasaEditrice(), req.getInCorso(), req.getStagioni(), req.getPiattaformaIds());

                Optional<Contenuto> created = contenutoDao.newContenuto(
                    req.getTitolo(),
                    req.getDescrizione(),
                    req.getGenere(),
                    req.getLink(),
                    req.getTipo(),
                    req.getAnnoPubblicazione(),
                    req.getCasaProduzione(),
                    req.getCasaEditrice(),
                    req.getInCorso(),
                    req.getStagioni(),
                    req.getImageLink(),
                    req.getPiattaformaIds());

            return created.<ResponseEntity<?>>map(c -> {
                try {
                    return ResponseEntity.created(new URI("/api/newContenuto/" + c.getId())).body(c);
                } catch (URISyntaxException e) {
                    return ResponseEntity.status(201).body(c);
                }
            }).orElseGet(() -> ResponseEntity.status(500).body(Map.of("message", "Failed to create contenuto")));
        } catch (Exception ex) {
            log.error("Error creating contenuto", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    // @DeleteMapping("/contenuto/delete/{id}")
    // public ResponseEntity<?> deleteContenuto(@PathVariable Long id) {
    //     try {
    //         boolean deleted = contenutoDao.deleteContenuto(id);
    //         if (!deleted) {
    //             return ResponseEntity.status(404).body(Map.of("message", "Contenuto non trovato"));
    //         }
    //         return ResponseEntity.ok(Map.of("message", "Contenuto eliminato"));
    //     } catch (Exception ex) {
    //         log.error("Error deleting contenuto id={}", id, ex);
    //         return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
    //     }
    // }

    // @PutMapping("/contenuto/update/{id}")
    // public ResponseEntity<?> updateContenuto(@PathVariable Long id, @RequestBody Contenuto req) {
    //     try {
    //         log.debug("Updating contenuto id={} payload tipo={} titolo={}", id, req.getTipo(), req.getTitolo());
    //         Optional<Contenuto> updated = contenutoDao.updateContenuto(
    //             id,
    //             req.getTitolo(),
    //             req.getDescrizione(),
    //             req.getGenere(),
    //             req.getLink(),
    //             req.getTipo(),
    //             req.getAnnoPubblicazione(),
    //             req.getCasaProduzione(),
    //             req.getCasaEditrice(),
    //             req.getInCorso(),
    //             req.getStagioni(),
    //             req.getImageLink());

    //         if (updated.isEmpty()) {
    //             return ResponseEntity.status(404).body(Map.of("message", "Contenuto non trovato"));
    //         }

    //         return ResponseEntity.ok(updated.get());
    //     } catch (Exception ex) {
    //         log.error("Error updating contenuto id={} ", id, ex);
    //         return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
    //     }
    // }

    @GetMapping("/contenuti")
    public ResponseEntity<?> getAllContenuti() {
        try {
            return ResponseEntity.ok(contenutoDao.findAll());
        } catch (Exception ex) {
            log.error("Error fetching contenuti", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/contenuti/giochi")
    public ResponseEntity<?> getAllGiochi() {
        try {
            return ResponseEntity.ok(contenutoDao.findByTipo("GIOCO"));
        } catch (Exception ex) {
            log.error("Error fetching contenuti", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/contenuti/giochi/generi")
    public ResponseEntity<?> getGeneriGiochi() {
        try {
            return ResponseEntity.ok(contenutoDao.findDistinctGeneriByTipo("GIOCO"));
        } catch (Exception ex) {
            log.error("Error fetching generi for giochi", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

     @GetMapping("/contenuti/film")
    public ResponseEntity<?> getAllFilm() {
        try {
            return ResponseEntity.ok(contenutoDao.findByTipo("FILM"));
        } catch (Exception ex) {
            log.error("Error fetching contenuti", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

     @GetMapping("/contenuti/serie_tv")
    public ResponseEntity<?> getAllSerieTv() {
        try {
            return ResponseEntity.ok(contenutoDao.findByTipo("SERIE_TV"));
        } catch (Exception ex) {
            log.error("Error fetching contenuti", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
