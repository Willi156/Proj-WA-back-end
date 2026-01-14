package com.critiverse.model;

public class Piattaforma{
     private Long id;
    private String nome;
    private Long versione;
    

    public Piattaforma() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getVersione() { return versione; }
    public void setVersione(Long versione) { this.versione = versione; }
    
}