package example.ui.common;

import java.sql.SQLException;

public interface Validator <T> {

    void validate (T value) throws SQLException;

}
