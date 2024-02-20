package com.atomiczek.shoppinglist.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID userId;
    private String login;
    private String email;
    private String password;
    private String role;

    public UserDTO(String login, String email) {
        this.login = login;
        this.email = email;
    }
    public UserDTO(UUID userId, String login, String email, String role) {
        this.userId = userId;
        this.login = login;
        this.email = email;
        this.role = role;
    }

    public UserDTO(UUID userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public UserDTO(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
