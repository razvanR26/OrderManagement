package example.model;

import java.math.BigDecimal;

public class Product {

    private final int id;
    private final String name;
    private final BigDecimal price;
    private final int stock;

    public Product(int id, String name, BigDecimal price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString () {
        return "Product:" + "\n" + "id: " + id + "\n" + "name: " + name + "\n" + "price: " + price + "\n" + "stock: " + stock + "\n";
    }
}
