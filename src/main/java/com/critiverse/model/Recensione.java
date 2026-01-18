package com.critiverse.model;

import java.util.Date;

public class Recensione{
     private Long id;
    private String titolo;
    private String testo;
    private Integer voto;
    private Date data;
    private Long idUtente;
    private Long idContenuto;
    private String username;
    

    public Recensione() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public Long getIdUtente() { return idUtente; }
    public void setIdUtente(Long idUtente) { this.idUtente = idUtente; }

    public Long getIdContenuto() { return idContenuto; }
    public void setIdContenuto(Long idContenuto) { this.idContenuto = idContenuto; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
}