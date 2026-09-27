package example.service.order;

public class OrderIdMismatchException extends RuntimeException {
    public OrderIdMismatchException(String message) {
        super(message);
    }
}
