package br.com.wepdev.dscatalog.repositories;

import br.com.wepdev.dscatalog.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private final Pageable pageable = PageRequest.of(0, 20);

    @Test
    void deveBuscarTodosQuandoSemFiltro() {
        Long categoryId = null;
        String name = "";

        Page<Product> result = productRepository.find(categoryId, name, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    void deveFiltrarPorNome() {
        Long categoryId = null;
        String name = "tv";

        Page<Product> result = productRepository.find(categoryId, name, pageable);

        assertThat(result.getContent())
                .allMatch(product ->
                        product.getName().toLowerCase().contains("tv")
                );
    }

    @Test
    void deveFiltrarPorCategoria() {
        Long categoryId = 1L; // ajuste conforme seu import.sql
        String name = "";

        Page<Product> result = productRepository.find(categoryId, name, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        // Produto deve possuir a categoria informada
        assertThat(result.getContent())
                .allMatch(product ->
                        product.getCategories().stream()
                                .anyMatch(category -> category.getId().equals(categoryId))
                );
    }

    @Test
    void deveFiltrarPorCategoriaENome() {
        Long categoryId = 1L;
        String name = "book";

        Page<Product> result = productRepository.find(categoryId, name, pageable);

        assertThat(result.getContent())
                .allMatch(product ->
                        product.getName().toLowerCase().contains("book")
                                && product.getCategories().stream()
                                .anyMatch(category -> category.getId().equals(categoryId))
                );
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoEncontrar() {
        Long categoryId = 999L;
        String name = "produto-inexistente";

        Page<Product> result = productRepository.find(categoryId, name, pageable);

        assertThat(result.getContent()).isEmpty();
    }
}
