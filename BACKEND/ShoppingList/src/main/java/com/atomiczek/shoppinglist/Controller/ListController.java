package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.DTO.ListDTO;
import com.atomiczek.shoppinglist.DTO.ProductDTO;
import com.atomiczek.shoppinglist.Entity.Bought;
import com.atomiczek.shoppinglist.Entity.Lists;
import com.atomiczek.shoppinglist.Entity.Products;
import com.atomiczek.shoppinglist.Entity.Users;
import com.atomiczek.shoppinglist.Repository.BoughtRepository;
import com.atomiczek.shoppinglist.Repository.ListRepository;
import com.atomiczek.shoppinglist.Repository.ProductsRepository;
import com.atomiczek.shoppinglist.Repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.atomiczek.shoppinglist.enums.BoughtEnum.BOUGHT;
import static com.atomiczek.shoppinglist.enums.BoughtEnum.NOT_BOUGHT;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ListController {

    @Autowired
    ListRepository listRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductsRepository productsRepository;

    @Autowired
    BoughtRepository boughtRepository;

    private static final Logger logger = LogManager.getLogger(ListController.class);

    @GetMapping("/getUserLists/{id}")
    public ResponseEntity<List<ListDTO>> getUserLists(@PathVariable("id") UUID id) {
        try {
            Users user = new Users(id);
            List<Lists> userLists = listRepository.findAllByAssignedById(user);
            List<ListDTO> userListsDTO = new ArrayList<>();
            for (Lists list : userLists) {
                ListDTO listDTO = new ListDTO(list.getListsId(), list.getListName());
                userListsDTO.add(listDTO);
            }
            return new ResponseEntity<>(userListsDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Exception during getting user lists: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addNewList/{id}")
    public ResponseEntity<ListDTO> addNewList(@PathVariable("id") UUID id, @RequestBody String listName) {
        try {
            Lists list = new Lists(listName);
            Users user = userRepository.findByUsersId(id);
            list.setAssignedById(user);

            listRepository.save(list);
            ListDTO listDTO = new ListDTO(list.getListsId(), list.getListName());
            return new ResponseEntity<>(listDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Exception during adding list: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/removeList/{id}")
    public ResponseEntity<HttpStatus> removeList(@PathVariable("id") UUID id) {
        try {
            Lists list = listRepository.findByListsId(id);
            if (list != null) {
                listRepository.delete(list);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            logger.warn("No list with id " + id);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Exception during deleting list: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateListName/{listId}")
    public ResponseEntity<ListDTO> updateListName(@PathVariable("listId") UUID listId, @RequestBody String listName) {
        try {
            Lists list = listRepository.findByListsId(listId);
            if (list != null) {
                list.setListName(listName);
                listRepository.save(list);
                ListDTO listDTO = new ListDTO(listId, listName);
                return new ResponseEntity<>(listDTO, HttpStatus.OK);
            } else {
                logger.warn("No list with id " + listId);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Exception during list name update: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getListName/{listId}")
    public ResponseEntity<ListDTO> getListName(@PathVariable("listId") UUID listId) {
        try {
            Lists list = listRepository.findByListsId(listId);
            if (list != null) {
                ListDTO listDTO = new ListDTO(listId, list.getListName());
                return new ResponseEntity<>(listDTO, HttpStatus.OK);
            } else {
                logger.warn("No list with id " + listId);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Exception during getting list name: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getProducts/{listId}")
    public ResponseEntity<List<ProductDTO>> getProducts(@PathVariable("listId") UUID listId) {
        try {
            Lists list = listRepository.findByListsId(listId);
            if (list != null) {
                List<ProductDTO> productsDTO = new ArrayList<>();
                for (Bought bought : list.getBought()) {
                    Products product = bought.getProduct();
                    ProductDTO productDTO = new ProductDTO(product.getProductsId(), product.getProductName(), bought.getBought());
                    productsDTO.add(productDTO);
                }
                return new ResponseEntity<>(productsDTO, HttpStatus.CREATED);
            }
            logger.warn("No list with id " + listId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Exception during getting all products by list: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("changeBought/{listId}")
    public ResponseEntity<ProductDTO> changeBought(@PathVariable("listId") UUID listId, @RequestBody ProductDTO productDTO) {
        try {
            Lists list = listRepository.findByListsId(listId);
            Products product = productsRepository.findByProductsId(productDTO.getProductId());
            if (list == null || product == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            Bought bought = boughtRepository.findByListAndProduct(list, product);
            if (bought.getBought() == NOT_BOUGHT.getValue()) {
                bought.setBought(BOUGHT.getValue());
                boughtRepository.save(bought);
            } else {
                bought.setBought(NOT_BOUGHT.getValue());
                boughtRepository.save(bought);
            }
            productDTO.setBought(bought.getBought());
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception during change of bought status: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
