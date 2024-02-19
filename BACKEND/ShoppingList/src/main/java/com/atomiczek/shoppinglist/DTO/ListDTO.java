package com.atomiczek.shoppinglist.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListDTO {
    private UUID listsId;
    private String listName;

    @Override
    public String toString() {
        return "ListDTO{" +
                "listsId=" + listsId +
                ", listName='" + listName + '\'' +
                '}';
    }
}
