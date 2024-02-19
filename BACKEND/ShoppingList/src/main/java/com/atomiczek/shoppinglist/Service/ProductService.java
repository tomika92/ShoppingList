package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.DTO.ProductDTO;
import com.atomiczek.shoppinglist.Entity.Bought;
import com.atomiczek.shoppinglist.Entity.Lists;
import com.atomiczek.shoppinglist.Entity.Products;
import com.atomiczek.shoppinglist.Repository.BoughtRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.atomiczek.shoppinglist.enums.BoughtEnum.DELETED;

@Service
public class ProductService {

    private final BoughtRepository boughtRepository;

    public ProductService(BoughtRepository boughtRepository) {this.boughtRepository = boughtRepository;}

    public List<ProductDTO> collectProductData(Lists list, List<Products> allProducts, List<ProductDTO> allProductsDTO) {
        for(Products product : allProducts){
            ProductDTO productDTO = new ProductDTO(product.getProductsId(), product.getProductName(), DELETED.getValue());
            Bought bought = boughtRepository.findByListAndProduct(list, product);
            if(bought != null){
                productDTO.setBought(bought.getBought());
            }
            allProductsDTO.add(productDTO);
        }
        return allProductsDTO;
    }
}
