package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.Entity.Role;
import com.atomiczek.shoppinglist.Entity.Users;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.atomiczek.shoppinglist.enums.UserRoleEnum.ADMIN;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService userService = new UserService(null);

    @Test
    public void shouldBeAdminTest() {
        Role role = new Role();
        role.setRole(ADMIN);
        role.setUsers(new ArrayList<>());

        Users users = new Users();
        users.setRoles(List.of(role));

        assertEquals(UserRoleEnum.ADMIN, userService.checkIsAdmin(users));

    }

}