package com.critiverse.model;

public class SerieTV{
    private Long idContenuto;
    private Boolean inCorso;
    private Integer stagioni;
    

    public SerieTV() {}

    public Long getIdContenuto() { return idContenuto; }
    public void setIdContenuto(Long idContenuto) { this.idContenuto = idContenuto; }

    public Boolean getInCorso() { return inCorso; }
    public void setInCorso(Boolean inCorso) { this.inCorso = inCorso; }
    
    public Integer getStagioni() { return stagioni; }
    public void setStagioni(Integer stagioni) { this.stagioni = stagioni; }
    
}