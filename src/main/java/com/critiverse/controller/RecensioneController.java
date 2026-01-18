package com.critiverse.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiverse.dao.RecensioneDao;


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

    
}
