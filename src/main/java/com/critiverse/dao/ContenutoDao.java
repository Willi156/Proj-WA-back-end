package com.critiverse.dao;

import java.util.List;
import java.util.Optional;

import com.critiverse.model.Contenuto;

public interface ContenutoDao {
    Optional<Contenuto> newContenuto(String titolo, String descrizione, String genere, String link, String tipo, Integer annoPubblicazione,
            String casaProduzione, String casaEditrice, Boolean inCorso, Integer stagioni, String imageLink);

    List<Contenuto> findAll();
    List<Contenuto> findByTipo(String tipo);
    List<String> findDistinctGeneriByTipo(String tipo);
}
