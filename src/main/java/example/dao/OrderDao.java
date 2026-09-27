package example.dao;

import example.model.Order;
import example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDao {

    public Optional <Order> findById (int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT id, customer_id, date " +
                    "FROM orders WHERE id = ?")) {
                preparedStatement.setInt(1, id);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new Order(
                               rs.getInt("id"),
                               rs.getInt("customer_id"),
                               rs.getTimestamp("date").toLocalDateTime()));
                    } else return Optional.empty();
                }
            }
        }
    }

    public List <Order> findAll () throws SQLException {
        List <Order> ordersList = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT id, customer_id, date FROM orders ORDER BY id")) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        ordersList.add(new Order(
                                rs.getInt("id"),
                                rs.getInt("customer_id"),
                                rs.getTimestamp("date").toLocalDateTime()));
                    }
                }
            }
        }
        return ordersList;
    }

    public int addOrder (Order order, Connection c) throws SQLException {
        int insertedRows;
        try (PreparedStatement preparedStatement = c.prepareStatement("INSERT INTO orders (id, customer_id, date) " +
                    "VALUES (?, ?, ?)")) {
                preparedStatement.setInt(1, order.getId());
                preparedStatement.setInt(2, order.getCustomerId());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(order.getDate()));
                insertedRows = preparedStatement.executeUpdate();
        }
        return insertedRows;
    }


    public int updateOrder (Order order) throws SQLException {
        int updatedRows;
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("UPDATE orders SET customer_id = ? " +
                    "WHERE id = ?")) {
                preparedStatement.setInt(1, order.getCustomerId());
                preparedStatement.setInt(2, order.getId());
                updatedRows = preparedStatement.executeUpdate();
            }
        }
        return updatedRows;
    }
}
