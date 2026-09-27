package example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection () throws SQLException {
       return DriverManager.getConnection(
               System.getenv("DB_URL"),
               System.getenv("DB_USER"),
               System.getenv("DB_PASSWORD"));
    }
}
