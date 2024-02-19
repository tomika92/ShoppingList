package com.atomiczek.shoppinglist.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productsId;
    private String productName;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Bought> bought;

    public List<Bought> getBought() {
        return bought;
    }

    @Override
    public String toString() {
        return "Products{" +
                "productsId=" + productsId +
                ", productName='" + productName + '\'' +
                '}';
    }
}
