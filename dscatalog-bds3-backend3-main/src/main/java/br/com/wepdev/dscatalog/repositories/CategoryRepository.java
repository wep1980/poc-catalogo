package br.com.wepdev.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.wepdev.dscatalog.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
