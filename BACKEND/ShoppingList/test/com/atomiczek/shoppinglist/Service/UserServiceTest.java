package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.DTO.UserDTO;
import com.atomiczek.shoppinglist.Entity.Role;
import com.atomiczek.shoppinglist.Entity.Users;
import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.atomiczek.shoppinglist.enums.UserRoleEnum.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
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

    @Test
    public void shouldEncodePassword() throws NoSuchAlgorithmException {
        String password = "password";
        String expectedHash = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";

        assertEquals(expectedHash, userService.encodeSHA256(password));
    }

    @Test
    public void shouldCreateUser() throws NoSuchAlgorithmException {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("testUserLogin");
        userDTO.setPassword("password");
        Role userRole = new Role();
        userRole.setRole(UserRoleEnum.USER);

        RoleRepository roleRepository = mock(RoleRepository.class);
        when(roleRepository.findByRole(UserRoleEnum.USER)).thenReturn(userRole);

        UserService userService = new UserService(roleRepository);
        Users user = new Users("testUserLogin", userService.encodeSHA256("password"));
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        user.setRoles(roles);
        Users createdUser = userService.createUser(userDTO);

        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getPassword(), createdUser.getPassword());
        assertEquals(user.getRoles(), createdUser.getRoles());
    }
}