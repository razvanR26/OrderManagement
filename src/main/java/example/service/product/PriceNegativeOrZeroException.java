package example.service.product;

public class PriceNegativeOrZeroException extends RuntimeException {
    public PriceNegativeOrZeroException(String message) {
        super(message);
    }
}
