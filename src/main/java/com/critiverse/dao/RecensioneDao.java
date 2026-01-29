package com.critiverse.dao;

import java.util.List;
import java.util.Optional;

import java.util.Date;

import com.critiverse.model.Recensione;
import com.critiverse.model.RecensioneConContenuto;

public interface RecensioneDao{

    List<Recensione> findByContenutoId(Long contenutoId);

    List<RecensioneConContenuto> findByUtenteIdWithContenuto(Long utenteId);

    boolean deleteById(Long recensioneId);

    Optional<Recensione> createRecensione(
            String titolo,
            String testo,
            Integer voto,
            Date data,
            Long idUtente,
            Long idContenuto);

    boolean updateRecensione(
            Long idRecensione,
            String titolo,
            String testo,
            Integer voto,
            Date data);
}
