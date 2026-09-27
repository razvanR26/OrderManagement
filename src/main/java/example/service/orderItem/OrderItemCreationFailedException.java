package example.service.orderItem;

public class OrderItemCreationFailedException extends RuntimeException {
    public OrderItemCreationFailedException(String message) {
        super(message);
    }
}
