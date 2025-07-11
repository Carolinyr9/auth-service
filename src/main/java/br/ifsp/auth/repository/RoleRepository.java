package br.ifsp.auth.repository;

import br.ifsp.auth.model.Role;
import br.ifsp.auth.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}
