package com.critiverse.model;

public class Utente {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private String username;
    private String password;
    private String ruolo;
    private String immagineProfilo;

    public Utente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    public String getImmagineProfilo() { return immagineProfilo; }
    public void setImmagineProfilo(String immagineProfilo) { this.immagineProfilo = immagineProfilo; }
}