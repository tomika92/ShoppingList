package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.DTO.ListDTO;
import com.atomiczek.shoppinglist.DTO.ProductDTO;
import com.atomiczek.shoppinglist.Entity.*;
import com.atomiczek.shoppinglist.Repository.BoughtRepository;
import com.atomiczek.shoppinglist.Repository.ListRepository;
import com.atomiczek.shoppinglist.Repository.ProductsRepository;
import com.atomiczek.shoppinglist.Repository.UserRepository;
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

import static com.atomiczek.shoppinglist.enums.BoughtEnum.BOUGHT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListControllerTest {

    @Mock
    ListRepository listRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductsRepository productsRepository;

    @Mock
    BoughtRepository boughtRepository;

    @InjectMocks
    ListController listController;

    @Test
    public void shouldGetAllUserLists(){
        UUID userID = UUID.randomUUID();
        List<ListDTO> userDTOLists = new ArrayList<>();
        List<Lists> userLists = new ArrayList<>();
        Users user = new Users(userID);
        userLists.add(new Lists("list1"));
        userLists.add(new Lists("list2"));
        ListDTO userDTO1 = new ListDTO();
        userDTO1.setListName("list1");
        ListDTO userDTO2 = new ListDTO();
        userDTO2.setListName("list2");
        userDTOLists.add(userDTO1);
        userDTOLists.add(userDTO2);

        when(listRepository.findAllByAssignedById(user)).thenReturn(userLists);

        ResponseEntity<List<ListDTO>> response = listController.getUserLists(userID);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTOLists, response.getBody());
        verify(listRepository).findAllByAssignedById(user);
    }

    /*@Test
    public void shouldAddNewListIfUserExist(){
        UUID userID = UUID.randomUUID();
        String listName = "New test list";
        Users user = new Users(userID);
        Lists list = new Lists(listName);
        ListDTO listDTO = new ListDTO(UUID.randomUUID(), list.getListName());

        when(userRepository.findByUsersId(userID)).thenReturn(user);
        when(listRepository.save(list)).thenReturn(any(Lists.class)).thenReturn(list);

        ResponseEntity<ListDTO> response = listController.addNewList(userID, listName);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(listDTO, response.getBody());
        verify(listRepository).save(any(Lists.class));
    }*/

    @Test
    public void shouldNotAddListAndReturnInternalServerError() {
        UUID userId = UUID.randomUUID();
        String listName = "Test list";

        when(userRepository.findByUsersId(userId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<ListDTO> response = listController.addNewList(userId, listName);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void shouldRemoveList() {
        UUID listID = UUID.randomUUID();
        Lists list = new Lists();

        when(listRepository.findByListsId(listID)).thenReturn(list);

        ResponseEntity<HttpStatus> response = listController.removeList(listID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(listRepository, times(1)).delete(list);
    }

    @Test
    public void shouldUpdateListName() {
        String listName = "New list name";
        UUID listID = UUID.randomUUID();
        Lists list = new Lists();
        list.setListName(listName);

        when(listRepository.findByListsId(listID)).thenReturn(list);

        ResponseEntity<ListDTO> response = listController.updateListName(listID, listName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listID, response.getBody().getListsId());
        assertEquals(listName, response.getBody().getListName());
        verify(listRepository, times(1)).save(list);
    }

    @Test
    public void shouldGetListName() {
        UUID listID = UUID.randomUUID();
        String listName = "List name";
        Lists list = new Lists(listName);

        when(listRepository.findByListsId(listID)).thenReturn(list);

        ResponseEntity<ListDTO> response = listController.getListName(listID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listID, response.getBody().getListsId());
        assertEquals(listName, response.getBody().getListName());
    }

    @Test
    public void shouldNotGetListName(){
        UUID listID = UUID.randomUUID();

        when(listRepository.findByListsId(listID)).thenReturn(null);

        ResponseEntity<ListDTO> response = listController.getListName(listID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void shouldFoundProductsOnList() {
        UUID listID = UUID.randomUUID();
        List<ProductDTO> productsDTO = new ArrayList<>();
        Lists list = new Lists();
        Products product = new Products();
        Long productID = 1L;
        product.setProductsId(productID);
        BoughtKey boughtKey = new BoughtKey(listID, product.getProductsId());
        Bought bought = new Bought(boughtKey, list, product, 2);
        List<Bought> boughtList = new ArrayList<>();
        boughtList.add(bought);
        list.setBought(boughtList);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productID);
        productDTO.setBought(BOUGHT.getValue());
        productsDTO.add(productDTO);

        when(listRepository.findByListsId(listID)).thenReturn(list);

        ResponseEntity<List<ProductDTO>> response = listController.getProducts(listID);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(productsDTO, response.getBody());
    }

    @Test
    public void shouldChangeBoughtValueOn1() {
        UUID listID = UUID.randomUUID();
        Long productID = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productID);
        productDTO.setBought(BOUGHT.getValue());
        Lists list = new Lists();
        list.setListsId(listID);
        Products product = new Products();
        product.setProductsId(productID);
        BoughtKey boughtKey = new BoughtKey(listID, product.getProductsId());
        Bought bought = new Bought(boughtKey, list, product, 2);

        when(listRepository.findByListsId(listID)).thenReturn(list);
        when(productsRepository.findByProductsId(productID)).thenReturn(product);
        when(boughtRepository.findByListAndProduct(list, product)).thenReturn(bought);

        ResponseEntity<ProductDTO> response = listController.changeBought(listID, productDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getBought());
    }
}
