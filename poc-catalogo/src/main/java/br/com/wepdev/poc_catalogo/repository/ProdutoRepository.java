package br.com.wepdev.poc_catalogo.repository;


import br.com.wepdev.poc_catalogo.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto,Long> {
}
