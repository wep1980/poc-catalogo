package br.com.wepdev.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.wepdev.dscatalog.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
