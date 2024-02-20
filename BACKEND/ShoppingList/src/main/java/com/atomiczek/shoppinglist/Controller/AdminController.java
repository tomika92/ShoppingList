package com.atomiczek.shoppinglist.Controller;

import com.atomiczek.shoppinglist.DTO.UserDTO;
import com.atomiczek.shoppinglist.Entity.*;
import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.Repository.UserDetailsRepository;

import com.atomiczek.shoppinglist.Repository.UserRepository;
import com.atomiczek.shoppinglist.Service.UserService;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import com.atomiczek.shoppinglist.kafka.KafkaProducerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    public static final String LOGGED_IN_MESSAGE = " account with this Id has been removed when user was logged in";
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    private final KafkaProducerService kafkaProducerService;

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Autowired
    public AdminController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    public AdminController(UserRepository userRepository, UserDetailsRepository userDetailsRepository, RoleRepository roleRepository, UserService userService, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        try{
            List<Users> allUsers = userRepository.findAll();
            return userService.createUsersDto(allUsers);
        } catch (Exception e){
            logger.error("Exception during getting all users: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/removeUser/{userId}")
    @Transactional
    public ResponseEntity<HttpStatus> removeUser(@PathVariable("userId") UUID userId){
        try{
            Users user = userRepository.findByUsersId(userId);
            if(user != null){
                int logged = user.getLogged();
                userDetailsRepository.delete(user.getDetails());
                if(user.getRoles() != null) {
                    user.getRoles().clear();
                }
                userRepository.delete(user);

                if(logged == 1){
                    kafkaProducerService.sendUserIdMessage(userId);
                    kafkaProducerService.sendUserRemoveMessage(LOGGED_IN_MESSAGE);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }
            logger.warn("No user with this id " + userId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.error("Exception during user remove: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/changeAdminRole/{userId}")
    public ResponseEntity<UserDTO> changeAdminRole(@PathVariable("userId") UUID userId, @RequestBody UserDTO userDTO){
        try{
            Users user = userRepository.findByUsersId(userId);
            if(user != null){
                String role;
                Role adminRole = roleRepository.findByRole(UserRoleEnum.ADMIN);
                if(UserRoleEnum.ADMIN.name().equals(userDTO.getRole())){
                    user.getRoles().remove(adminRole);
                    role = UserRoleEnum.USER.name();
                }
                else {
                    user.getRoles().add(adminRole);
                    role = UserRoleEnum.ADMIN.name();
                }
                userRepository.save(user);
                userDTO.setRole(role);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
            else {
                logger.warn("No user with this id " + userId);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            logger.error("Exception during admin role changing: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchByLoginOrEmail/{query}")
    public ResponseEntity<List<UserDTO>> searchByLoginOrEmail(@PathVariable String query){
        try{
            List<Users> matchingUsersList = new ArrayList<>(userRepository.findByLoginContaining(query));

            List<Users_details> allEmailMatching = userDetailsRepository.findByEmailContaining(query);
            for (Users_details userDetails : allEmailMatching) {
                Users user = userDetails.getUsers();
                if (!matchingUsersList.contains(user)) {
                    matchingUsersList.add(user);
                }
            }

            return userService.createUsersDto(matchingUsersList);
        } catch (Exception e){
            logger.error("Exception during searching users: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
