package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.Entity.Role;
import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class RoleInitializationService implements CommandLineRunner {

    public RoleInitializationService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    private RoleRepository roleRepository;

    private static final Logger logger = LogManager.getLogger(RoleInitializationService.class);
    @Override
    public void run(String... args) {
        initializeRoles();
    }

    void initializeRoles() {
        long count = roleRepository.count();
        if(count == 0){
            for (UserRoleEnum userRole : UserRoleEnum.values()) {
                Role role = new Role();
                role.setRole(userRole);
                roleRepository.save(role);
            }
        }
        else {
            logger.info("Skipping role insertion");
        }
    }
}
