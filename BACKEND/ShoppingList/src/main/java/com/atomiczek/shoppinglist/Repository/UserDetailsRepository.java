package com.atomiczek.shoppinglist.Repository;

import com.atomiczek.shoppinglist.Entity.Users_details;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDetailsRepository extends JpaRepository<Users_details, Long> {

    Users_details findByEmail(String email);

    List<Users_details> findByEmailContaining(String email);
}
