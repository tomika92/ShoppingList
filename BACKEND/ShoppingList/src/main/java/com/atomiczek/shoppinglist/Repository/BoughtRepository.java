package com.atomiczek.shoppinglist.Repository;

import com.atomiczek.shoppinglist.Entity.Bought;
import com.atomiczek.shoppinglist.Entity.Lists;
import com.atomiczek.shoppinglist.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoughtRepository extends JpaRepository<Bought, Long> {

    Bought findByListAndProduct(Lists listId, Products productId);
}
