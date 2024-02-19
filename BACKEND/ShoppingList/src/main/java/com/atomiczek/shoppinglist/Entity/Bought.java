package com.atomiczek.shoppinglist.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bought {
    @EmbeddedId
    BoughtKey id;

    @ManyToOne
    @MapsId("list_id")
    @JoinColumn(name = "list_id")
    Lists list;

    @ManyToOne
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    Products product;

    int bought;

    public Bought(Lists list, Products product, int bought) {
        this.list = list;
        this.product = product;
        this.bought = bought;
    }

    public Lists getList() {
        return list;
    }

    public Products getProduct() {
        return product;
    }

    public void setList(Lists list) {
        this.list = list;
        this.list.getBought().add(this);
    }

}
