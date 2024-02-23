package com.atomiczek.shoppinglist.Entity;

import com.atomiczek.shoppinglist.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long roleId;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @ManyToMany(mappedBy = "roles")
    private List<Users> users;

    public List<Users> getUsers() {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }
}

