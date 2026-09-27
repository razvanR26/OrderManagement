package example.dao;

import example.model.Customer;
import example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDao {

    public Optional <Customer> findById (int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT id, lname, fname, email " +
                    "FROM customers WHERE id = ?")) {
                preparedStatement.setInt(1, id);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                     return Optional.of(new Customer(
                             rs.getInt("id"),
                             rs.getString("lname"),
                             rs.getString("fname"),
                             rs.getString("email")));
                    } else return Optional.empty();
                }
            }
        }
    }

    public List <Customer> findAll () throws SQLException {
        List <Customer> customersList = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT id, lname, fname, email FROM customers ORDER BY id")) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        customersList.add(new Customer(
                                rs.getInt("id"),
                                rs.getString("lname"),
                                rs.getString("fname"),
                                rs.getString("email")
                        ));
                    }
                }
            }
        }
        return customersList;
    }

    public int addCustomer (Customer customer) throws SQLException {
        int insertedRows;
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("INSERT INTO customers (id, lname, fname, email) " +
                    "VALUES (?, ?, ?, ?)")) {
                preparedStatement.setInt(1, customer.getId());
                preparedStatement.setString(2, customer.getLname());
                preparedStatement.setString(3, customer.getFname());
                preparedStatement.setString(4, customer.getEmail());
                insertedRows = preparedStatement.executeUpdate();
            }
        }
        return insertedRows;
    }

    public int updateCustomer (Customer customer) throws SQLException {
        int updatedRows;
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("UPDATE customers SET lname = ?, fname = ?, email = ? " +
                    "WHERE id = ?")) {
                preparedStatement.setString(1, customer.getLname());
                preparedStatement.setString(2, customer.getFname());
                preparedStatement.setString(3, customer.getEmail());
                preparedStatement.setInt(4, customer.getId());
                updatedRows = preparedStatement.executeUpdate();
            }
        }
        return updatedRows;
    }

}
