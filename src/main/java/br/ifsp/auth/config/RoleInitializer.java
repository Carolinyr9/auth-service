package br.ifsp.auth.config;

import br.ifsp.auth.model.Role;
import br.ifsp.auth.model.enums.RoleName;
import br.ifsp.auth.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class RoleInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleInitializer.class);
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("--- CHECKING/INITIALIZING ROLES ---");
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByRoleName(roleName).orElseGet(() -> {
                log.info("Creating role: " + roleName);
                return roleRepository.save(new Role(roleName));
            });
        }
        log.info("--- ROLES INITIALIZATION COMPLETE ---");
    }
}
