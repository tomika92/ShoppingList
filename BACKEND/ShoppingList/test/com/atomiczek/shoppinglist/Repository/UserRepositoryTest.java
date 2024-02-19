package com.atomiczek.shoppinglist.Repository;

import com.atomiczek.shoppinglist.Entity.Users;
import com.atomiczek.shoppinglist.ShoppingListApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = ShoppingListApplication.class)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void shouldReturnAtLeastOneUser() {
        List<Users> users = userRepository.findAll();
        assertNotEquals(0, users.size());
    }

}