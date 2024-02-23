package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.DTO.ProductDTO;
import com.atomiczek.shoppinglist.Entity.Bought;
import com.atomiczek.shoppinglist.Entity.BoughtKey;
import com.atomiczek.shoppinglist.Entity.Lists;
import com.atomiczek.shoppinglist.Entity.Products;
import com.atomiczek.shoppinglist.Repository.BoughtRepository;
import com.atomiczek.shoppinglist.Repository.ListRepository;
import com.atomiczek.shoppinglist.Repository.ProductsRepository;
import com.atomiczek.shoppinglist.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.atomiczek.shoppinglist.enums.BoughtEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

    @Test
    public void shouldGetAllProducts() {
        UUID listID = UUID.randomUUID();
        List<ProductDTO> productDTOList = new ArrayList<>();
        Lists list = new Lists();
        list.setListsId(listID);
        List<Products> productsList = new ArrayList<>();

        when(listRepository.findByListsId(listID)).thenReturn(list);
        when(productsRepository.findAll()).thenReturn(productsList);
        when(productService.collectProductData(list,productsList, productDTOList)).thenReturn(productDTOList);

        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts(listID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDTOList, response.getBody());
    }

    @Test
    public void shouldAddProductToList() {
        UUID listID = UUID.randomUUID();
        ProductDTO productDTO = new ProductDTO();
        long productID = 1L;
        Products product = new Products();
        product.setProductsId(productID);
        Lists list = new Lists();
        list.setListsId(listID);
        BoughtKey boughtKey = new BoughtKey(listID, product.getProductsId());
        Bought bought = new Bought(list, product, NOT_BOUGHT.getValue());
        bought.setId(boughtKey);
        productDTO.setBought(bought.getBought());
        productDTO.setProductId(productID);

        when(productsRepository.findByProductsId(productID)).thenReturn(product);
        when(listRepository.findByListsId(listID)).thenReturn(list);

        ResponseEntity<ProductDTO> response = productController.addProductToList(listID, productDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
        verify(boughtRepository,times(1)).save(bought);
    }

    @Test
    public void shouldRemoveProductFromList() {
        UUID listID = UUID.randomUUID();
        long productID = 1L;
        String productName = "Product name";
        ProductDTO productDTO = new ProductDTO(productID, productName, DELETED.getValue());
        Products product = new Products();
        product.setProductsId(productID);
        product.setProductName(productName);
        Lists list = new Lists();
        list.setListsId(listID);
        Bought bought = new Bought(list, product, BOUGHT.getValue());

        when(productsRepository.findByProductsId(productID)).thenReturn(product);
        when(listRepository.findByListsId(listID)).thenReturn(list);
        when(boughtRepository.findByListAndProduct(list, product)).thenReturn(bought);

        ResponseEntity<ProductDTO> response = productController.removeProductFromList(listID, productID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
        verify(boughtRepository, times(1)).delete(bought);
    }

    @Test
    public void shouldFindProductByName() {
        UUID listID = UUID.randomUUID();
        String query = "Name";
        long productID = 1L;
        List<ProductDTO> productDTOList = new ArrayList<>();
        List<Products> productsList = new ArrayList<>();
        Products product = new Products();
        product.setProductsId(productID);
        Lists list = new Lists();
        list.setListName("List Name");
        list.setListsId(listID);
        productsList.add(product);

        when(productsRepository.findByProductNameContaining(query)).thenReturn(productsList);
        when(listRepository.findByListsId(listID)).thenReturn(list);
        when(productService.collectProductData(list,productsList,productDTOList)).thenReturn(productDTOList);

        ResponseEntity<List<ProductDTO>> response = productController.searchByName(listID, query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDTOList, response.getBody());
    }

    @Test
    public void shouldNotFindProductByName() {
        UUID listID = UUID.randomUUID();
        String query = "Name";
        List<Products> productsList = new ArrayList<>();
        Lists list = new Lists();
        List<ProductDTO> productDTOList = new ArrayList<>();

        when(productsRepository.findByProductNameContaining(query)).thenReturn(productsList);
        when(listRepository.findByListsId(listID)).thenReturn(list);
        when(productService.collectProductData(list,productsList,productDTOList)).thenReturn(productDTOList);

        ResponseEntity<List<ProductDTO>> response = productController.searchByName(listID, query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
    }
}

