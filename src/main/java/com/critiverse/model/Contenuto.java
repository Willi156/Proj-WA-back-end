package com.critiverse.model;

public class Contenuto{
     private Long id;
    private String titolo;
    private String descrizione;
    private String genere;
    private String link;
    private String tipo;
    private Integer annoPubblicazione;
    // Valutazione media (nullable) recuperata da recensioni
    private Double mediaVoti;

    // Opzionali
	private String casaProduzione;
	private String casaEditrice;
	private Boolean inCorso;
	private Integer stagioni;

    private String imageLink;
    

    public Contenuto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getAnnoPubblicazione() { return annoPubblicazione; }
    public void setAnnoPubblicazione(Integer annoPubblicazione) { this.annoPubblicazione = annoPubblicazione; }

    public String getCasaProduzione() { return casaProduzione; }
    public void setCasaProduzione(String casaProduzione) { this.casaProduzione = casaProduzione; }

    public String getCasaEditrice() { return casaEditrice; }
    public void setCasaEditrice(String casaEditrice) { this.casaEditrice = casaEditrice; }

    public Boolean getInCorso() { return inCorso; }
    public void setInCorso(Boolean inCorso) { this.inCorso = inCorso; }

    public Integer getStagioni() { return stagioni; }
    public void setStagioni(Integer stagioni) { this.stagioni = stagioni; }

    public Double getMediaVoti() { return mediaVoti; }
    public void setMediaVoti(Double mediaVoti) { this.mediaVoti = mediaVoti; }

    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }
}