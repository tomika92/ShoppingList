package com.atomiczek.shoppinglist.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users_details")
public class Users_details {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long usersDetailsId;
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private Users users;

    public Users_details(String email) {
        this.email = email;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Users_details{" +
                "usersDetailsId=" + usersDetailsId +
                ", email='" + email + '\'' +
                '}';
    }
}
