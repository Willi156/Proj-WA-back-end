package com.critiverse.model;

public class Piattaforma {
    private Long id;
    private String nome;
    private String versione;

    public Piattaforma() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getVersione() { return versione; }
    public void setVersione(String versione) { this.versione = versione; }
}