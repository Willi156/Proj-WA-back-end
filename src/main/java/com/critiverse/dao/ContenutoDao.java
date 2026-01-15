package com.critiverse.dao;

import java.util.Optional;

import com.critiverse.model.Contenuto;

public interface ContenutoDao {
    Optional<Contenuto> newContenuto(String titolo, String descrizione, String genere, String link, String tipo, String annoPubblicazione);
}
