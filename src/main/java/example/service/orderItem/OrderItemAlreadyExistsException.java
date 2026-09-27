package example.service.orderItem;

public class OrderItemAlreadyExistsException extends RuntimeException {
    public OrderItemAlreadyExistsException(String message) {
        super(message);
    }
}
