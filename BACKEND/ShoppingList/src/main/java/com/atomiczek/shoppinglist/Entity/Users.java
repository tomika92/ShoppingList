package com.atomiczek.shoppinglist.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID usersId;
    private String login;
    private String password;
    @OneToOne(mappedBy = "users")
    private Users_details details;
    private LocalDate createdAt;
    private int logged;
    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
    @OneToMany(mappedBy = "assignedById", cascade = CascadeType.ALL)
    private List<Lists> listsID = new ArrayList<>();

    public Users(String login, String password) {
        this.login = login;
        this.password = password;
        this.createdAt = LocalDate.now();
        this.logged = 0;
    }

    public Users(UUID id) {
        this.usersId = id;
    }

    public Users_details getUsersDetails(){
        return details;
    }

    public void setUsers_details(Users_details details){
        this.details = details;
        this.details.setUsers(this);
    }

    public List<Lists> getListsID() {
        return listsID;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
        roles.forEach(
                role -> role.getUsers().add(this)
        );
    }

    @Override
    public String toString() {
        return "Users{" +
                "usersId=" + usersId +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", logged=" + logged +
                ", roles='" + roles + '\'' +
                ", listsID=" + listsID +
                '}';
    }
}
