package br.com.wepdev.poc_catalogo.service;

import br.com.wepdev.poc_catalogo.domain.Categoria;
import br.com.wepdev.poc_catalogo.domain.Produto;
import br.com.wepdev.poc_catalogo.dto.CategoriaDTO;
import br.com.wepdev.poc_catalogo.dto.ProdutoDTO;
import br.com.wepdev.poc_catalogo.exception.ProdutoNaoEncontradoException;
import br.com.wepdev.poc_catalogo.repository.CategoriaRepository;
import br.com.wepdev.poc_catalogo.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // =====================
    // findAllPaged
    // =====================

    @Test
    void deveRetornarProdutosPaginados() {
        Produto produto = new Produto();
        produto.setNome("Notebook");

        Page<Produto> page = new PageImpl<>(
                List.of(produto),
                PageRequest.of(0, 10),
                1
        );

        when(produtoRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<ProdutoDTO> result = produtoService.findAllPaged(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Notebook", result.getContent().get(0).getNome());
    }

    // =====================
    // findById
    // =====================

    @Test
    void deveBuscarProdutoPorId() {
        Produto produto = new Produto();
        produto.setNome("Mouse");

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        ProdutoDTO dto = produtoService.findById(1L);

        assertEquals("Mouse", dto.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> produtoService.findById(99L)
        );
    }

    // =====================
    // adicionar
    // =====================

    @Test
    void deveAdicionarProdutoComCategorias() {
        Categoria categoria = new Categoria("Eletronicos");
        categoria.setId(2L);

        when(categoriaRepository.getReferenceById(1L))
                .thenReturn(categoria);

        when(produtoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoDTO dto = new ProdutoDTO();
        dto.setNome("Notebook");
        dto.setDescricao("Ultrafino");
        dto.setPreco(BigDecimal.valueOf(3000));
        dto.setImagemUrl("img.png");
        dto.setData(OffsetDateTime.now());

        CategoriaDTO catDto = new CategoriaDTO();
        catDto.setId(1L);

        dto.setCategorias(List.of(catDto));

        ProdutoDTO result = produtoService.adicionar(dto);

        assertEquals("Notebook", result.getNome());
    }

    // =====================
    // atualizar
    // =====================

    @Test
    void deveAtualizarProdutoComSucesso() {
        Produto produto = new Produto();
        produto.setNome("Antigo");

        when(produtoRepository.getReferenceById(1L))
                .thenReturn(produto);

        when(categoriaRepository.getReferenceById(any()))
                .thenReturn(new Categoria("Categoria"));

        when(produtoRepository.save(any()))
                .thenReturn(produto);

        ProdutoDTO dto = new ProdutoDTO();
        dto.setNome("Novo");
        dto.setCategorias(List.of(new CategoriaDTO(1L, "Categoria")));

        ProdutoDTO result = produtoService.atualizar(1L, dto);

        assertEquals("Novo", result.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarProdutoInexistente() {
        when(produtoRepository.getReferenceById(99L))
                .thenThrow(EntityNotFoundException.class);

        ProdutoDTO dto = new ProdutoDTO();

        assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> produtoService.atualizar(99L, dto)
        );
    }

    // =====================
    // remover
    // =====================

    @Test
    void deveRemoverProdutoComSucesso() {
        doNothing().when(produtoRepository).deleteById(1L);

        produtoService.remover(1L);

        verify(produtoRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExisteAoRemover() {
        doThrow(EmptyResultDataAccessException.class)
                .when(produtoRepository)
                .deleteById(99L);

        assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> produtoService.remover(99L)
        );
    }
}
