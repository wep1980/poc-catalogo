package br.com.wepdev.poc_catalogo.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_categoria")
public class Categoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String nome;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE") // Armazenando o registro criado com fuso horário
    private OffsetDateTime registroCriado;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE") // Atualizando o registro com fuso horário
    private OffsetDateTime registroAtualizado;


    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria() {
    }

    public OffsetDateTime getRegistroCriado() {
        return registroCriado;
    }

    public OffsetDateTime getRegistroAtualizado() {
        return registroAtualizado;
    }

    @PrePersist
    public void prePersist() {
        this.registroCriado = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.registroAtualizado = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
