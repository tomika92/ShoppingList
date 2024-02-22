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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.atomiczek.shoppinglist.enums.BoughtEnum.DELETED;
import static com.atomiczek.shoppinglist.enums.BoughtEnum.NOT_BOUGHT;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    ProductsRepository productsRepository;

    @Autowired
    ListRepository listRepository;

    @Autowired
    BoughtRepository boughtRepository;

    @Autowired
    private ProductService productService;

    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @GetMapping("/getAllProducts/{listId}")
    public ResponseEntity<List<ProductDTO>> getAllProducts(@PathVariable("listId") UUID listId){
        try{
            Lists list = listRepository.findByListsId(listId);
            if(list == null) {
                logger.warn("Found 0 products");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            List<Products> allProducts = productsRepository.findAll();
            List<ProductDTO> allProductsDTO = new ArrayList<>();

            allProductsDTO = productService.collectProductData(list, allProducts, allProductsDTO);
            return new ResponseEntity<>(allProductsDTO, HttpStatus.OK);
        } catch(Exception e){
            logger.error("Exception during getting all products: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addProductToList/{listId}")
    public ResponseEntity<ProductDTO> addProductToList(@PathVariable("listId") UUID listId, @RequestBody ProductDTO productDTO){
        try{
            Products product = productsRepository.findByProductsId(productDTO.getProductId());
            Lists list = listRepository.findByListsId(listId);
            if(product == null || list == null){
                logger.warn("No products with this products ID " + productDTO.getProductId() + " and list ID " + listId);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            BoughtKey boughtKey = new BoughtKey(listId, product.getProductsId());
            Bought bought = new Bought(list, product, NOT_BOUGHT.getValue());
            bought.setId(boughtKey);
            boughtRepository.save(bought);
            productDTO.setBought(bought.getBought());
            return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
        } catch(Exception e){
            logger.error("Exception during adding product to list: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/removeProductFromList/{listId}/{productId}")
    public ResponseEntity<ProductDTO> removeProductFromList(@PathVariable("listId") UUID listId, @PathVariable("productId") Long productId){
        try{
            Products product = productsRepository.findByProductsId(productId);
            Lists list = listRepository.findByListsId(listId);
            if(product == null || list == null){
                logger.warn("No products with this products ID " + productId + " or list ID " + listId);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            Bought bought = boughtRepository.findByListAndProduct(list, product);
            if(bought == null){
                logger.warn("No product on list" + listId);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            ProductDTO productDTO = new ProductDTO(productId, product.getProductName(), DELETED.getValue());

            boughtRepository.delete(bought);
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        } catch(Exception e){
            logger.error("Exception during deleting product from list: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchByName/{listId}")
    public ResponseEntity<List<ProductDTO>> searchByName(@PathVariable("listId") UUID listId, @RequestParam String query){
        try{
            List<Products> matchingProductsList = productsRepository.findByProductNameContaining(query);
            List<ProductDTO> matchingProductsDTOList = new ArrayList<>();
            Lists list = listRepository.findByListsId(listId);

            matchingProductsDTOList = productService.collectProductData(list, matchingProductsList, matchingProductsDTOList);

            if(matchingProductsDTOList.isEmpty()){
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(matchingProductsDTOList, HttpStatus.OK);
        } catch(Exception e){
            logger.error("Exception during products search: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
