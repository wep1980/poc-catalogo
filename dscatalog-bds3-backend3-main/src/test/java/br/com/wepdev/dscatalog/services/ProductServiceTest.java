package br.com.wepdev.dscatalog.services;

import br.com.wepdev.dscatalog.domain.Category;
import br.com.wepdev.dscatalog.domain.Product;
import br.com.wepdev.dscatalog.dto.ProductDTO;
import br.com.wepdev.dscatalog.repositories.CategoryRepository;
import br.com.wepdev.dscatalog.repositories.ProductRepository;
import br.com.wepdev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private Product product;
    private Category category;

    @BeforeEach
    void setup() {
        category = new Category(1L, "Livros");

        product = new Product(
                1L,
                "Livro Java",
                "Descrição",
                100.0,
                "img.png",
                Instant.now()
        );
        product.getCategories().add(category);
    }

    @Test
    void deveBuscarProdutosPaginados() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(repository.find(any(), anyString(), any())).thenReturn(page);
        when(repository.findProductsWithCategories(any())).thenReturn(List.of(product));

        Page<ProductDTO> result = service.findAllPaged(0L, "", PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getName()).isEqualTo("Livro Java");
    }

    @Test
    void deveBuscarProdutoPorIdQuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveInserirProduto() {
        when(repository.save(any())).thenReturn(product);
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        ProductDTO dto = new ProductDTO(product, Set.of(category));

        ProductDTO result = service.insert(dto);

        assertThat(result.getName()).isEqualTo("Livro Java");
    }

    @Test
    void deveExcluirProdutoQuandoIdExiste() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        doThrow(ResourceNotFoundException.class).when(repository).deleteById(99L);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
