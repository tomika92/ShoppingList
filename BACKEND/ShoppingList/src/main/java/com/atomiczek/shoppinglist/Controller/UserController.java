package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.DTO.UserDTO;
import com.atomiczek.shoppinglist.Entity.Users;
import com.atomiczek.shoppinglist.Entity.Users_details;
import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.Repository.UserDetailsRepository;
import com.atomiczek.shoppinglist.Repository.UserRepository;
import com.atomiczek.shoppinglist.Service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        try {
            if ((userRepository.findByLogin(userDTO.getLogin()) != null) || userDetailsRepository.findByEmail(userDTO.getEmail()) != null) {
                logger.info("There is user with this email or login");
                return new ResponseEntity<>(null, HttpStatus.FOUND);
            }

            Users user = userService.createUser(userDTO);

            Users_details users_details = new Users_details(userDTO.getEmail());
            user.setUsers_details(users_details);

            userRepository.save(user);
            userDetailsRepository.save(users_details);

            UserDTO userDTOTemp = new UserDTO(user.getLogin(), users_details.getEmail());
            logger.info("New user registered " + user.getLogin());
            return new ResponseEntity<>(userDTOTemp, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Exception during user creation: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/login")
    public ResponseEntity<UserDTO> getUser(@RequestBody UserDTO userDTO) {
        try {
            String encodedPass = userService.encodeSHA256(userDTO.getPassword());
            Users user = userRepository.findByLoginAndPassword(userDTO.getLogin(), encodedPass);
            if (user != null) {
                String role = userService.checkIsAdmin(user).name();

                if (user.getLogged() == 0) {
                    user.setLogged(1);
                    userRepository.save(user);
                }

                UserDTO userDTOTemp = new UserDTO(user.getUsersId(), role);
                logger.info("User " + userDTO.getLogin() + " logged");
                return new ResponseEntity<>(userDTOTemp, HttpStatus.ACCEPTED);
            } else {
                logger.info("User " + userDTO.getLogin() + " not found");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Exception during user logging: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/logout/{id}")
    public ResponseEntity<UUID> logoutUser(@PathVariable("id") UUID id) {
        try {
            Users user = userRepository.findByUsersId(id);
            if (user.getLogged() == 1) {
                user.setLogged(0);
                userRepository.save(user);
            }
            logger.info("User " + user.getLogin() + " logged out");
            return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Exception during user logging out: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserRole/{id}")
    public ResponseEntity<UserDTO> getUserRole(@PathVariable("id") UUID id) {
        try {
            Users user = userRepository.findByUsersId(id);
            if (user != null) {
                String role = userService.checkIsAdmin(user).name();
                UserDTO userDTO = new UserDTO(id, role);
                logger.info("Got user " + user.getLogin() + " role");
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                logger.warn("No user with this id " + id);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Exception during getting user role: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
