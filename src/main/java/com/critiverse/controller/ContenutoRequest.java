package com.critiverse.controller;

public class ContenutoRequest {
	private String titolo;
	private String genere;
	private String link;
	private String tipo;
	private Integer annoPubblicazione; // expected yyyy-MM-dd

	public String getTitolo() { return titolo; }
	public void setTitolo(String titolo) { this.titolo = titolo; }

	// Optional type-specific fields (nullable)
	private String casaProduzione;
	private String casaEditrice;
	private Boolean inCorso;
	private Integer stagioni;

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

	public String getGenere() { return genere; }
	public void setGenere(String genere) { this.genere = genere; }

	public String getLink() { return link; }
	public void setLink(String link) { this.link = link; }

	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }
}
