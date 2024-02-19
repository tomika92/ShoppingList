package com.atomiczek.shoppinglist.Repository;

import com.atomiczek.shoppinglist.Entity.Role;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRole(UserRoleEnum userRoleEnum);
}