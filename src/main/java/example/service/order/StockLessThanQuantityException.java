package example.service.order;

public class StockLessThanQuantityException extends RuntimeException {
    public StockLessThanQuantityException(String message) {
        super(message);
    }
}
