package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class Order {
    private final String id;
    private final List<Product> products;
    private final Long orderTime;
    private final String author;
    private String status;

    public Order(String id, List<Product> products, Long orderTime, String author) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products must not be empty");
        }

        this.id = id;
        this.products = products;
        this.orderTime = orderTime;
        this.author = author;
        this.status = OrderStatus.WAITING_PAYMENT.getValue();
    }

    public Order(String id, List<Product> products, Long orderTime, String author, String status) {
        this(id, products, orderTime, author);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        if (OrderStatus.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Invalid order status");
        }
    }

}
