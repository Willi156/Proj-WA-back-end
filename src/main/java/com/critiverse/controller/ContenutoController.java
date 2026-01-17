package com.critiverse.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
                    req.getStagioni());

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

    @GetMapping("/contenuti")
    public ResponseEntity<?> getAllContenuti() {
        try {
            return ResponseEntity.ok(contenutoDao.findAll());
        } catch (Exception ex) {
            log.error("Error fetching contenuti", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
