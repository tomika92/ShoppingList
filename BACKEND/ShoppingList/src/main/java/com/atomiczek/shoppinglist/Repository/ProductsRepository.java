package com.atomiczek.shoppinglist.Repository;

import com.atomiczek.shoppinglist.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {

    Products findByProductsId(Long productId);

    List<Products> findByProductNameContaining(String name);
}
