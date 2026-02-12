package br.com.wepdev.dscatalog.services;

import br.com.wepdev.dscatalog.domain.Category;
import br.com.wepdev.dscatalog.dto.CategoryDTO;
import br.com.wepdev.dscatalog.repositories.CategoryRepository;
import br.com.wepdev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Category category;

    @BeforeEach
    void setup() {
        category = new Category(1L, "Livros");
    }

    @Test
    void deveBuscarTodasCategoriasPaginadas() {
        Page<Category> page = new PageImpl<>(List.of(category));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<CategoryDTO> result = service.findAllPaged(PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getName()).isEqualTo("Livros");
    }

    @Test
    void deveBuscarCategoriaPorIdQuandoIdExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveInserirCategoria() {
        when(repository.save(any())).thenReturn(category);

        CategoryDTO dto = new CategoryDTO(null, "Livros");
        CategoryDTO result = service.insert(dto);

        assertThat(result.getName()).isEqualTo("Livros");
    }

    @Test
    void deveExcluirCategoriaQuandoIdExiste() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirCategoriaInexistente() {
        doThrow(ResourceNotFoundException.class).when(repository).deleteById(99L);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
