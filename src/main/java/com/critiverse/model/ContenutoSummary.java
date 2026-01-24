package com.critiverse.model;

public class ContenutoSummary {
    private Long id;
    private String titolo;
    private String tipo;

    public ContenutoSummary() {}

    public ContenutoSummary(Long id, String titolo, String tipo) {
        this.id = id;
        this.titolo = titolo;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
