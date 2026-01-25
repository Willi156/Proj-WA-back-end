package com.critiverse.model;

import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Lazy-loading proxy for RecensioneConContenuto collection of an Utente.
 */
public class UtenteProxy extends Utente {

    private List<RecensioneConContenuto> recensioni;

    @JsonIgnore
    private final Function<Long, List<RecensioneConContenuto>> loader;

    public UtenteProxy(Utente base, Function<Long, List<RecensioneConContenuto>> loader) {
        this.loader = loader;
        this.copyFrom(base);
    }

    public synchronized List<RecensioneConContenuto> getRecensioni() {
        if (recensioni == null && loader != null && getId() != null) {
            recensioni = loader.apply(getId());
        }
        return recensioni;
    }

    private void copyFrom(Utente base) {
        this.setId(base.getId());
        this.setNome(base.getNome());
        this.setCognome(base.getCognome());
        this.setEmail(base.getEmail());
        this.setUsername(base.getUsername());
        this.setPassword(base.getPassword());
        this.setRuolo(base.getRuolo());
        this.setImmagineProfilo(base.getImmagineProfilo());
    }
}
