package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.Repository.BoughtRepository;
import com.atomiczek.shoppinglist.Repository.ListRepository;
import com.atomiczek.shoppinglist.Repository.ProductsRepository;
import com.atomiczek.shoppinglist.Service.ProductService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductsRepository productsRepository;

    @Mock
    ListRepository listRepository;

    @Mock
    BoughtRepository boughtRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    ProductController productController;

}

