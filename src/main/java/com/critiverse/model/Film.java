package com.critiverse.model;

public class Film{
     private Long idContenuto;
    private String casaProduzione;
    

    public Film() {}

    public Long getIdContenuto() { return idContenuto; }
    public void setIdContenuto(Long idContenuto) { this.idContenuto = idContenuto; }

    public String getCasaProduzione() { return casaProduzione; }
    public void setCasaProduzione(String casaProduzione) { this.casaProduzione = casaProduzione; }
    
}