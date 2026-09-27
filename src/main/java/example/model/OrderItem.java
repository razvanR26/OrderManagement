package example.model;

public class OrderItem {

    private final int orderId;
    private final int productId;
    private final int quantity;

    public OrderItem (int orderId, int productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString () {
        return "OrderItem:" + "\n" + "orderId: " + orderId + "\n" + "productId: " + productId + "\n" + "quantity: " + quantity + "\n";
    }
}
