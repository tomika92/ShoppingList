package com.atomiczek.shoppinglist.Repository;

import com.atomiczek.shoppinglist.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {

    Users findByLogin(String login);
    Users findByPassword(String password);
    Users findByUsersId(UUID id);
    Users findByLoginAndPassword(String login, String password);
    List<Users> findByLoginContaining(String login);

}
