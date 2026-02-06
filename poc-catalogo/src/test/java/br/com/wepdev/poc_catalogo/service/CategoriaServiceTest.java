package br.com.wepdev.poc_catalogo.service;

import br.com.wepdev.poc_catalogo.domain.Categoria;
import br.com.wepdev.poc_catalogo.dto.CategoriaDTO;
import br.com.wepdev.poc_catalogo.exception.CategoriaNaoEncontradaException;
import br.com.wepdev.poc_catalogo.exception.DatabaseException;
import br.com.wepdev.poc_catalogo.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // =====================
    // findAllPaged
    // =====================

    @Test
    void deveRetornarCategoriasPaginadas() {
        Categoria categoria = new Categoria("Eletrônicos");

        Page<Categoria> page = new PageImpl<>(
                List.of(categoria),
                PageRequest.of(0, 10),
                1
        );

        when(categoriaRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<CategoriaDTO> result = categoriaService.findAllPaged(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Eletrônicos", result.getContent().get(0).getNome());
    }

    // =====================
    // findById
    // =====================

    @Test
    void deveBuscarCategoriaPorIdComSucesso() {
        Categoria categoria = new Categoria("Livros");

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        CategoriaDTO dto = categoriaService.findById(1L);

        assertEquals("Livros", dto.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExiste() {
        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoriaNaoEncontradaException.class,
                () -> categoriaService.findById(99L)
        );
    }

    // =====================
    // adicionar
    // =====================

    @Test
    void deveAdicionarCategoria() {
        Categoria categoria = new Categoria("Games");

        when(categoriaRepository.save(any()))
                .thenReturn(categoria);

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNome("Games");

        CategoriaDTO result = categoriaService.adicionar(dto);

        assertEquals("Games", result.getNome());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    // =====================
    // atualizar
    // =====================

    @Test
    void deveAtualizarCategoriaComSucesso() {
        Categoria categoria = new Categoria("Antigo");

        when(categoriaRepository.getReferenceById(1L))
                .thenReturn(categoria);

        when(categoriaRepository.save(any()))
                .thenReturn(categoria);

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNome("Novo");

        CategoriaDTO result = categoriaService.atualizar(1L, dto);

        assertEquals("Novo", result.getNome());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarCategoriaInexistente() {
        when(categoriaRepository.getReferenceById(99L))
                .thenThrow(EntityNotFoundException.class);

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNome("Teste");

        assertThrows(
                CategoriaNaoEncontradaException.class,
                () -> categoriaService.atualizar(99L, dto)
        );
    }

    // =====================
    // remover
    // =====================

    @Test
    void deveRemoverCategoriaComSucesso() {
        when(categoriaRepository.existsById(1L))
                .thenReturn(true);

        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.remover(1L);

        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoRemoverInexistente() {
        when(categoriaRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                CategoriaNaoEncontradaException.class,
                () -> categoriaService.remover(99L)
        );

        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void deveLancarDatabaseExceptionQuandoViolacaoDeIntegridade() {
        when(categoriaRepository.existsById(1L))
                .thenReturn(true);

        doThrow(DataIntegrityViolationException.class)
                .when(categoriaRepository)
                .deleteById(1L);

        assertThrows(
                DatabaseException.class,
                () -> categoriaService.remover(1L)
        );
    }
}

