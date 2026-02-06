package br.com.wepdev.poc_catalogo.dto;

import br.com.wepdev.poc_catalogo.domain.Categoria;

import java.io.Serializable;

public class CategoriaDTO implements Serializable {

    private Long id;
    private String nome;

    public CategoriaDTO() {}

    public CategoriaDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
    }


    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
