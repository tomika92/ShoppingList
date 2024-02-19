package com.atomiczek.shoppinglist.Service;

import com.atomiczek.shoppinglist.DTO.UserDTO;
import com.atomiczek.shoppinglist.Entity.Role;
import com.atomiczek.shoppinglist.Repository.RoleRepository;
import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import com.atomiczek.shoppinglist.Entity.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.atomiczek.shoppinglist.enums.UserRoleEnum.ADMIN;


@Service
public class UserService {

    private final RoleRepository roleRepository;

    public UserService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public UserRoleEnum checkIsAdmin(Users user){
        for (Role userRole : user.getRoles()) {
            if (userRole.getRole() == ADMIN) {
                return ADMIN;
            }
        }
        return UserRoleEnum.USER;
    }

    public String encodeSHA256(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] passwordBytes = password.getBytes();
        byte[] hash = messageDigest.digest(passwordBytes);
        StringBuilder hexHash = new StringBuilder();
        for (byte b : hash) {
            hexHash.append(String.format("%02x", b));
        }
        return String.valueOf(hexHash);
    }


    public Users createUser(UserDTO userDTO) throws NoSuchAlgorithmException {
        String encodedPass = encodeSHA256(userDTO.getPassword());

        Users user = new Users(userDTO.getLogin(), encodedPass);
        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findByRole(UserRoleEnum.USER);
        roles.add(userRole);
        user.setRoles(roles);
        return user;
    }

    public ResponseEntity<List<UserDTO>> createUsersDto(List<Users> allUsers) {
        List<UserDTO> allUsersDTO = new ArrayList<>();
        for (Users user : allUsers) {
            String role = checkIsAdmin(user).name();
            UserDTO userDTO = new UserDTO(user.getUsersId(), user.getLogin(), user.getUsersDetails().getEmail(), role);
            allUsersDTO.add(userDTO);
        }
        return new ResponseEntity<>(allUsersDTO, HttpStatus.OK);
    }
}
