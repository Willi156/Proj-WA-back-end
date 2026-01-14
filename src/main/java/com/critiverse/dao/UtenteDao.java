package com.critiverse.dao;

import com.critiverse.model.Utente;
import java.util.Optional;

public interface UtenteDao {
    Optional<Utente> findFirst();
}
