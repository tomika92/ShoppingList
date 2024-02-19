package com.atomiczek.shoppinglist.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdMessage {
    private UUID userId;

    @Override
    public String toString() {
        return  "" + userId + "";
    }
}
