package br.com.wepdev.dscatalog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.wepdev.dscatalog.domain.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
    SELECT DISTINCT obj
    FROM Product obj
    JOIN obj.categories cats
    WHERE
    (:categoryId IS NULL OR cats.id = :categoryId)
    AND
    LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%'))""")
    Page<Product> find(Long categoryId, String name, Pageable pageable);


    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")
    List<Product> findProductsWithCategories(List<Product> products);

}


