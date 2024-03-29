package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.DTO.UserDTO;
import com.atomiczek.shoppinglist.Entity.Role;
import com.atomiczek.shoppinglist.Entity.Users;
import com.atomiczek.shoppinglist.Entity.Users_details;
import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.Repository.UserDetailsRepository;
import com.atomiczek.shoppinglist.Repository.UserRepository;
import com.atomiczek.shoppinglist.Service.UserService;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import com.atomiczek.shoppinglist.kafka.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserDetailsRepository userDetailsRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    UserService userService;

    @Mock
    KafkaProducerService kafkaProducerService;

    @InjectMocks
    AdminController adminController;

    @Test
    public void shouldGetAllUsersList(){
        List<Users> usersList = new ArrayList<>();
        List<UserDTO> usersDTOList = new ArrayList<>();
        Users user1 = new Users();
        Users user2 = new Users();
        Users_details users_details1 = new Users_details("test@email1.com");
        Users_details users_details2 = new Users_details("test@email2.com");
        user1.setUsers_details(users_details1);
        user2.setUsers_details(users_details2);
        user1.setLogin("testLogin1");
        user2.setLogin("testLogin2");
        usersList.add(user1);
        usersList.add(user2);
        usersDTOList.add(new UserDTO(user1.getLogin(), users_details1.getEmail()));
        usersDTOList.add(new UserDTO(user2.getLogin(), users_details2.getEmail()));

        when(userRepository.findAll()).thenReturn(usersList);
        when(userService.createUsersDto(usersList)).thenReturn(new ResponseEntity<>(usersDTOList, HttpStatus.OK));

        ResponseEntity<List<UserDTO>> response = adminController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usersDTOList, response.getBody());
    }

    @Test
    public void shouldRemoveUser(){
        UUID userID = UUID.randomUUID();
        Users users = new Users();
        users.setUsersId(userID);

        when(userRepository.findByUsersId(userID)).thenReturn(users);

        ResponseEntity<HttpStatus> response = adminController.removeUser(userID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userDetailsRepository, times(1)).delete(users.getDetails());
        verify(userRepository, times(1)).delete(users);
    }

    @Test
    public void shouldRemoveLoggedUser(){
        UUID userID = UUID.randomUUID();
        Users users = new Users();
        users.setUsersId(userID);
        users.setLogged(1);

        when(userRepository.findByUsersId(userID)).thenReturn(users);

        ResponseEntity<HttpStatus> response = adminController.removeUser(userID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(kafkaProducerService, times(1)).sendUserIdMessage(userID);
        verify(kafkaProducerService, times(1)).sendUserRemoveMessage(AdminController.LOGGED_IN_MESSAGE);
    }

    @Test
    public void shouldChangeRoleOnUser(){
        UUID userID = UUID.randomUUID();
        Users users = new Users();
        users.setRoles(new ArrayList<>());
        Role admin = new Role();
        UserDTO userDTO = new UserDTO(userID, "USER");
        admin.setRole(UserRoleEnum.ADMIN);

        when(userRepository.findByUsersId(userID)).thenReturn(users);
        when(roleRepository.findByRole(UserRoleEnum.ADMIN)).thenReturn(admin);

        ResponseEntity<UserDTO> response = adminController.changeAdminRole(userID, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UserRoleEnum.ADMIN.name(), response.getBody().getRole());
        verify(userRepository, times(1)).save(users);
    }

    @Test
    public void shouldNotFoundUser(){
        UUID userID = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();

        when(userRepository.findByUsersId(userID)).thenReturn(null);

        ResponseEntity<UserDTO> response = adminController.changeAdminRole(userID, userDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void shouldFoundUserByLoginOrEmail(){
        String query = "Login1";
        List<UserDTO> userDTOList = new ArrayList<>();
        List<Users> usersList = new ArrayList<>();
        Users user1 = new Users();
        Users_details users_details1 = new Users_details("test@email1.com");
        user1.setUsers_details(users_details1);
        user1.setLogin("testLogin1");
        usersList.add(user1);
        userDTOList.add(new UserDTO(user1.getLogin(), users_details1.getEmail()));

        when(userRepository.findByLoginContaining(query)).thenReturn(Arrays.asList(user1));
        when(userDetailsRepository.findByEmailContaining(query)).thenReturn(new ArrayList<>());
        when(userService.createUsersDto(usersList)).thenReturn(new ResponseEntity<>(userDTOList, HttpStatus.OK));

        ResponseEntity<List<UserDTO>> response = adminController.searchByLoginOrEmail(query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTOList, response.getBody());
    }
}