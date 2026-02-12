package br.com.wepdev.dscatalog.repositories;

import br.com.wepdev.dscatalog.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void deveSalvarCategoria() {
        Category categoria = new Category();
        categoria.setName("Nova Categoria");

        Category resultado = categoryRepository.save(categoria);

        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getName()).isEqualTo("Nova Categoria");
    }

    @Test
    void deveBuscarPorIdQuandoIdExiste() {
        Category categoria = new Category();
        categoria.setName("Categoria Existente");
        categoria = categoryRepository.save(categoria);

        Optional<Category> resultado = categoryRepository.findById(categoria.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getName()).isEqualTo("Categoria Existente");
    }

    @Test
    void deveRetornarVazioQuandoIdNaoExiste() {
        Optional<Category> resultado = categoryRepository.findById(999L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveBuscarTodasCategorias() {
        Category c1 = new Category();
        c1.setName("Categoria 1");

        Category c2 = new Category();
        c2.setName("Categoria 2");

        categoryRepository.save(c1);
        categoryRepository.save(c2);

        List<Category> resultado = categoryRepository.findAll();

        assertThat(resultado).isNotEmpty();
        assertThat(resultado.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void deveExcluirCategoria() {
        Category categoria = new Category();
        categoria.setName("Categoria para excluir");
        categoria = categoryRepository.save(categoria);

        categoryRepository.deleteById(categoria.getId());

        Optional<Category> resultado = categoryRepository.findById(categoria.getId());

        assertThat(resultado).isEmpty();
    }
}
