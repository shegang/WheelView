package com.she.core.loopview;

import java.util.List;

public class ItemData<T> {
    private String ItemName;
    private T item;

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
