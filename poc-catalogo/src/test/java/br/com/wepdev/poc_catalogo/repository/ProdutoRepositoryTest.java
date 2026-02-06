package br.com.wepdev.poc_catalogo.repository;

import br.com.wepdev.poc_catalogo.domain.Categoria;
import br.com.wepdev.poc_catalogo.domain.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProdutoRepositoryTest {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    ProdutoRepositoryTest(
            ProdutoRepository produtoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // ======================
    // SAVE
    // ======================

    @Test
    void deveSalvarProdutoComCategoria() {
        Categoria categoria = categoriaRepository.save(new Categoria("Eletrônicos"));

        Produto produto = new Produto();
        produto.setNome("Notebook");
        produto.setDescricao("Ultrafino");
        produto.setPreco(BigDecimal.valueOf(3000));
        produto.setImagemUrl("img.png");
        produto.setData(OffsetDateTime.now());

        // 👉 forma correta (sem setter)
        produto.getCategorias().add(categoria);

        Produto salvo = produtoRepository.save(produto);

        assertNotNull(salvo.getId());
        assertEquals(1, salvo.getCategorias().size());
    }

    // ======================
    // FIND BY ID
    // ======================

    @Test
    void deveBuscarProdutoComCategorias() {
        Categoria categoria = categoriaRepository.save(new Categoria("Periféricos"));

        Produto produto = new Produto();
        produto.setNome("Mouse");

        produto.getCategorias().add(categoria);

        produto = produtoRepository.save(produto);

        Produto encontrado = produtoRepository.findById(produto.getId())
                .orElseThrow();

        assertEquals("Mouse", encontrado.getNome());
        assertFalse(encontrado.getCategorias().isEmpty());
    }

    // ======================
    // DELETE
    // ======================

    @Test
    void deveDeletarProduto() {
        Produto produto = produtoRepository.save(new Produto());

        produtoRepository.deleteById(produto.getId());

        assertTrue(produtoRepository.findById(produto.getId()).isEmpty());
    }
}
