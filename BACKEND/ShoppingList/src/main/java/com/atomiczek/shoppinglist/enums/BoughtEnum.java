package com.atomiczek.shoppinglist.enums;

public enum BoughtEnum {

    DELETED(0), NOT_BOUGHT(1), BOUGHT(2);
    private Integer value;

    BoughtEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
