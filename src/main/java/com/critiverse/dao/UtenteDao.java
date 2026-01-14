package com.critiverse.dao;

import com.critiverse.model.Utente;
import java.util.Optional;

public interface UtenteDao {
    Optional<Utente> findFirst();
    Optional<Utente> findByUsernameAndPassword(String username, String password);
}
