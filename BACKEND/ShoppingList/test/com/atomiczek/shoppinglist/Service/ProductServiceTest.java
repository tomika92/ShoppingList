package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.DTO.ProductDTO;
import com.atomiczek.shoppinglist.Entity.Bought;
import com.atomiczek.shoppinglist.Entity.Lists;
import com.atomiczek.shoppinglist.Entity.Products;
import com.atomiczek.shoppinglist.Repository.BoughtRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.atomiczek.shoppinglist.enums.BoughtEnum.BOUGHT;
import static com.atomiczek.shoppinglist.enums.BoughtEnum.NOT_BOUGHT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceTest {

    @Mock
    private BoughtRepository boughtRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void shouldCollectProductData() {
        List<Products> productsList = new ArrayList<>();
        Products product1 = new Products();
        Products product2 = new Products();
        product1.setProductsId(1L);
        product1.setProductName("ProductName1");
        product2.setProductsId(2L);
        product2.setProductName("ProductName2");
        productsList.add(product1);
        productsList.add(product2);
        List<ProductDTO> productDTOList = new ArrayList<>();
        Bought bought1 = new Bought();
        bought1.setBought(BOUGHT.getValue());
        Bought bought2 = new Bought();
        bought2.setBought(NOT_BOUGHT.getValue());
        Lists list = new Lists();

        when(boughtRepository.findByListAndProduct(list, product1)).thenReturn(bought1);
        when(boughtRepository.findByListAndProduct(list, product2)).thenReturn(bought2);

        List<ProductDTO> response = productService.collectProductData(list, productsList, productDTOList);

        assertEquals(productsList.size(), response.size());
        assertEquals("ProductName1", response.get(0).getProductName());
        assertEquals("ProductName2", response.get(1).getProductName());


    }
}