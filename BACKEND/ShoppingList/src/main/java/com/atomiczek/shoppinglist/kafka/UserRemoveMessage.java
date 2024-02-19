package com.atomiczek.shoppinglist.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRemoveMessage {
    private String message;

    @Override
    public String toString() {
        return "" + message + "";
    }
}
