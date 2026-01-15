package com.critiverse.controller;

public class ContenutoRequest {
	private String titolo;
	private String descrizione;
	private String genere;
	private String link;
	private String tipo;
	private Integer annoPubblicazione; // expected yyyy-MM-dd

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
}
