package com.atomiczek.shoppinglist.Repository;

import com.atomiczek.shoppinglist.Entity.Lists;
import com.atomiczek.shoppinglist.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListRepository extends JpaRepository<Lists, UUID> {
    List<Lists> findAllByAssignedById(Users id);

    Lists findByListsId(UUID id);
}
