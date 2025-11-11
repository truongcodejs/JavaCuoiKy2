package model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(MenuItem menuItem, int quantity) {
        for (CartItem item : items) {
            if (item.getMenuItem().getId() == menuItem.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(menuItem, quantity));
    }

    public void removeItem(MenuItem menuItem) {
        items.removeIf(item -> item.getMenuItem().getId() == menuItem.getId());
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public void clear() {
        items.clear();
    }
}