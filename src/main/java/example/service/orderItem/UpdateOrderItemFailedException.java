package example.service.orderItem;

public class UpdateOrderItemFailedException extends RuntimeException {
    public UpdateOrderItemFailedException(String message) {
        super(message);
    }
}
