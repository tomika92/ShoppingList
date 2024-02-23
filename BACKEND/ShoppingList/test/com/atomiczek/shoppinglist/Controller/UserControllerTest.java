package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.DTO.UserDTO;
import com.atomiczek.shoppinglist.Entity.Users;
import com.atomiczek.shoppinglist.Entity.Users_details;
import com.atomiczek.shoppinglist.Repository.UserDetailsRepository;
import com.atomiczek.shoppinglist.Repository.UserRepository;
import com.atomiczek.shoppinglist.Service.UserService;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserDetailsRepository userDetailsRepository;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Test
    public void shouldInsertUser() throws NoSuchAlgorithmException {
        UserDTO userDTO = new UserDTO("testLogin", "test@email.com");
        Users_details users_details = new Users_details(userDTO.getEmail());
        Users users = new Users();
        users.setLogin(userDTO.getLogin());
        users.setUsers_details(users_details);

        when(userRepository.findByLogin(anyString())).thenReturn(null);
        when(userDetailsRepository.findByEmail(anyString())).thenReturn(null);
        when(userService.createUser(any(UserDTO.class))).thenReturn(users);

        ResponseEntity<UserDTO> response = userController.addUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO.getLogin(), response.getBody().getLogin());
        assertEquals(userDTO.getEmail(), response.getBody().getEmail());
        verify(userRepository, times(1)).save(any(Users.class));
        verify(userDetailsRepository, times(1)).save(any(Users_details.class));
    }

    @Test
    public void shouldFindSameLoginOrEmailInDB(){
        UserDTO userDTO = new UserDTO("testLogin", "test@email.com");

        when(userRepository.findByLogin(anyString())).thenReturn(new Users());
        ResponseEntity<UserDTO> response = userController.addUser(userDTO);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, never()).save(any(Users.class));
        verify(userDetailsRepository, never()).save(any(Users_details.class));
    }

    @Test
    public void shouldFindUserAndLogin() throws NoSuchAlgorithmException {
        String encodedPass = "testPass";
        UserDTO userDTO = new UserDTO("testLogin", "test@email.com", encodedPass);
        Users users = new Users();

        when(userService.encodeSHA256(userDTO.getPassword())).thenReturn(encodedPass);
        when(userRepository.findByLoginAndPassword(anyString(), anyString())).thenReturn(users);
        when(userService.checkIsAdmin(users)).thenReturn(UserRoleEnum.USER);

        ResponseEntity<UserDTO> response = userController.getUser(userDTO);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(users.getLogin(), response.getBody().getLogin());
        assertEquals("USER", response.getBody().getRole());
        verify(userRepository, times(1)).save(users);
    }

    @Test
    public void shouldGetInternalServerError(){
        String encodedPass = "testPass";
        UserDTO userDTO = new UserDTO("testLogin", "test@email.com", encodedPass);

        when(userRepository.findByLoginAndPassword(anyString(), anyString())).thenThrow(new RuntimeException("Test exception"));
        ResponseEntity<UserDTO> response = userController.getUser(userDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    public void shouldLogout(){
        UUID userID = UUID.randomUUID();
        Users users = new Users();
        users.setLogged(1);

        when(userRepository.findByUsersId(userID)).thenReturn(users);
        ResponseEntity<UUID> response = userController.logoutUser(userID);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(userID, response.getBody());
        verify(userRepository, times(1)).findByUsersId(userID);
        verify(userRepository, times(1)).save(users);
    }

    @Test
    public void shouldFindUserAndRole(){
        UUID userID = UUID.randomUUID();
        Users users = new Users();
        users.setLogin("testLogin");

        when(userRepository.findByUsersId(userID)).thenReturn(users);
        when(userService.checkIsAdmin(users)).thenReturn(UserRoleEnum.ADMIN);

        ResponseEntity<UserDTO> response = userController.getUserRole(userID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userID, response.getBody().getUserId());
        assertEquals("ADMIN", response.getBody().getRole());
    }
}