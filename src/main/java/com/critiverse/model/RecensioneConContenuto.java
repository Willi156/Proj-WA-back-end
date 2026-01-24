package com.critiverse.model;

public class RecensioneConContenuto {
    private Recensione recensione;
    private Contenuto contenuto;

    public RecensioneConContenuto() {}

    public Recensione getRecensione() {
        return recensione;
    }

    public void setRecensione(Recensione recensione) {
        this.recensione = recensione;
    }

    public Contenuto getContenuto() {
        return contenuto;
    }

    public void setContenuto(Contenuto contenuto) {
        this.contenuto = contenuto;
    }
}
