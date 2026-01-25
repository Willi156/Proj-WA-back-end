package com.critiverse.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.critiverse.dao.PiattaformaDao;

@RestController
@RequestMapping("/api")
public class PiattaformaController {

    private static final Logger log = LoggerFactory.getLogger(PiattaformaController.class);

    private final PiattaformaDao piattaformaDao;

    public PiattaformaController(PiattaformaDao piattaformaDao) {
        this.piattaformaDao = piattaformaDao;
    }

    @GetMapping("/piattaforme/nomi")
    public ResponseEntity<?> getAllNames() {
        try {
            return ResponseEntity.ok(piattaformaDao.findAllNames());
        } catch (Exception ex) {
            log.error("Error fetching piattaforme", ex);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
