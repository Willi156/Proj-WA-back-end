package com.critiverse.model;

public class Preferiti{
    private Long id;
     private Long idUtente;
    private Long idContenuto;

    

    public Preferiti() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUtente() { return idUtente; }
    public void setIdUtente(Long idUtente) { this.idUtente = idUtente; }

    public Long getIdContenuto() { return idContenuto; }
    public void setIdContenuto(Long idContenuto) { this.idContenuto = idContenuto; }
    
}