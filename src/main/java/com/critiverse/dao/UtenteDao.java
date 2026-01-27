package com.critiverse.dao;

import java.util.Optional;

import com.critiverse.model.Utente;

public interface UtenteDao {
    Optional<Utente> findByUsernameAndPassword(String username, String password);
    Optional<Boolean> checkUsernameExists(String username);
    Optional<Utente> newUtente(String nome, String cognome, String email, String username, String password);
    Optional<Utente> findById(Long id);
    Optional<Utente> updateUtenteInfo(Long id, String nome, String cognome, String email, String immagineProfilo);
    boolean updatePassword(Long id, String newPassword);
    Optional<Boolean> verifyPassword(Long id, String password);
}
