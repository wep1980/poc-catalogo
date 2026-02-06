package br.com.wepdev.poc_catalogo.repository;

import br.com.wepdev.poc_catalogo.domain.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CategoriaRepositoryTest {

    private final CategoriaRepository categoriaRepository;

    CategoriaRepositoryTest(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Test
    void deveSalvarCategoria() {
        Categoria categoria = new Categoria("Eletronicos");

        Categoria salva = categoriaRepository.save(categoria);

        assertNotNull(salva.getId());
        assertEquals("Eletronicos", salva.getNome());
    }

    @Test
    void deveBuscarPorId() {
        Categoria categoria = categoriaRepository.save(new Categoria("Livros"));

        Optional<Categoria> result = categoriaRepository.findById(categoria.getId());

        assertTrue(result.isPresent());
        assertEquals("Livros", result.get().getNome());
    }

    @Test
    void deveDeletarCategoria() {
        Categoria categoria = categoriaRepository.save(new Categoria("Games"));

        categoriaRepository.deleteById(categoria.getId());

        Optional<Categoria> result = categoriaRepository.findById(categoria.getId());

        assertTrue(result.isEmpty());
    }
}
