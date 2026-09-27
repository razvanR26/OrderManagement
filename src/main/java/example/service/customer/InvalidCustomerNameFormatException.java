package example.service.customer;

public class InvalidCustomerNameFormatException extends RuntimeException {
    public InvalidCustomerNameFormatException(String message) {
        super(message);
    }
}
