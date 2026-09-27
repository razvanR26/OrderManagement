package example.service.product;

public class StockNegativeException extends RuntimeException {
    public StockNegativeException(String message) {
        super(message);
    }
}
