package example.service.product;

public class InvalidProductNameFormatException extends RuntimeException {
    public InvalidProductNameFormatException(String message) {
        super(message);
    }
}
