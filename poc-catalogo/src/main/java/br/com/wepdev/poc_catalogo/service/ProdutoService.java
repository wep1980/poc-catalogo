package br.com.wepdev.poc_catalogo.service;

import br.com.wepdev.poc_catalogo.domain.Categoria;
import br.com.wepdev.poc_catalogo.domain.Produto;
import br.com.wepdev.poc_catalogo.dto.CategoriaDTO;
import br.com.wepdev.poc_catalogo.dto.ProdutoDTO;
import br.com.wepdev.poc_catalogo.exception.ProdutoNaoEncontradoException;
import br.com.wepdev.poc_catalogo.repository.CategoriaRepository;
import br.com.wepdev.poc_catalogo.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> findAllPaged(Pageable pageable) {

        Page<Produto> produtos = produtoRepository.findAll(pageable);

        return produtos.map(ProdutoDTO::new);
    }

    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto com id " + id + " não encontrado"));

        return new ProdutoDTO(produto, produto.getCategorias());
    }

    @Transactional
    public ProdutoDTO adicionar(ProdutoDTO produtoDTO) {
        Produto produto = new Produto();
        copiarDtoParaEntidade(produtoDTO, produto);
        produto = produtoRepository.save(produto);

        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO atualizar(Long id ,ProdutoDTO produtoDTO) {
        try {
            Produto produto = produtoRepository.getReferenceById(id);

            copiarDtoParaEntidade(produtoDTO, produto);

            produto = produtoRepository.save(produto);

            return new ProdutoDTO(produto);

        }catch (EntityNotFoundException e) {
            throw new ProdutoNaoEncontradoException("Produto com id " + id + " não encontrado");
        }

    }

    @Transactional
    public void remover(Long id) {
        try {
            produtoRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ProdutoNaoEncontradoException("Violação de integridade referencial ao tentar deletar o produto com id " + id);
        }
    }

    private void copiarDtoParaEntidade(ProdutoDTO produtoDTO, Produto produto) {
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setImagemUrl(produtoDTO.getImagemUrl());
        produto.setData(produtoDTO.getData());

        produto.getCategorias().clear(); // Limpa as categorias existentes
        for (CategoriaDTO categoriaDTO : produtoDTO.getCategorias()) {
            Categoria categoria = categoriaRepository.getReferenceById(categoriaDTO.getId());
            produto.getCategorias().add(categoria);
        }
    }

}


