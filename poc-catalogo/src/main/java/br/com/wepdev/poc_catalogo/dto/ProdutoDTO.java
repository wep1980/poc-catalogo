package br.com.wepdev.poc_catalogo.dto;

import br.com.wepdev.poc_catalogo.domain.Categoria;
import br.com.wepdev.poc_catalogo.domain.Produto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@JsonPropertyOrder({"id","nome","descricao","preco","imagemUrl","data","categorias"})
public class ProdutoDTO implements Serializable {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private  String imagemUrl;
    private OffsetDateTime data;

    private List<CategoriaDTO> categorias = new ArrayList<>();

    public ProdutoDTO() {
    }

    public ProdutoDTO(Long id, String nome, String descricao, BigDecimal preco, String imagemUrl, OffsetDateTime data) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
        this.data = data;
    }

    public ProdutoDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.preco = produto.getPreco();
        this.imagemUrl = produto.getImagemUrl();
        this.data = produto.getData();
    }

    public ProdutoDTO(Produto produto, Set<Categoria> categorias) {
        this(produto);
        categorias.forEach(categoria -> this.categorias.add(new CategoriaDTO(categoria)));
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public OffsetDateTime getData() {
        return data;
    }

    public void setData(OffsetDateTime data) {
        this.data = data;
    }

    public List<CategoriaDTO> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaDTO> categorias) {
        this.categorias = categorias;
    }
}
