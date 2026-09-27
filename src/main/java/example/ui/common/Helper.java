package example.ui.common;

import java.sql.SQLException;
import java.util.InputMismatchException;

public class Helper {

    public static <T> T helpValidate (Prompter prompter, Reader <T> reader, Validator <T> validator, String fieldName) throws SQLException {
        T value;
        while (true) {
            try {
                prompter.show();
                value = reader.read();
                validator.validate(value);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid " + fieldName + ". Please enter a valid one");
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
        return value;
    }

    public static <T> T read (Prompter prompter, Reader <T> reader, String fieldName) {
        T value;
        while (true) {
            try {
                prompter.show();
                value = reader.read();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid " + fieldName + ". Please enter a valid one");
            }
        }
        return value;
    }
}
