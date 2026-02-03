package com.critiverse.dao;

import java.util.List;

import com.critiverse.model.Piattaforma;

public interface PiattaformaDao {
    List<String> findAllNames();
    List<Piattaforma> findAll();
}
