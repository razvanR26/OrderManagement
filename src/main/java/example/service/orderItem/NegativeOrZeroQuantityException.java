package example.service.orderItem;

public class NegativeOrZeroQuantityException extends RuntimeException {
    public NegativeOrZeroQuantityException(String message) {
        super(message);
    }
}
