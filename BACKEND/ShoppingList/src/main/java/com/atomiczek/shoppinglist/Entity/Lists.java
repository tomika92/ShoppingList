package com.atomiczek.shoppinglist.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lists")
public class Lists {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID listsId;
    private String listName;
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users assignedById;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL)
    private List<Bought> bought;

    public Lists(String listName) {
        this.listName = listName;
        this.createdAt = LocalDate.now();
    }

    public void setAssignedById(Users assignedById) {
        this.assignedById = assignedById;
        this.assignedById.getListsID().add(this);
    }

    public List<Bought> getBought() {
        return bought;
    }

    @Override
    public String toString() {
        return "Lists{" +
                "listsId=" + listsId +
                ", listName='" + listName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
