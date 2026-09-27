package example.dao;

import example.model.OrderItem;
import example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderItemDao {

    public Optional <OrderItem> findById (int orderId, int productId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT order_id, product_id, quantity " +
                    "FROM Order_Items WHERE order_id = ? AND product_id = ?")) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, productId);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                      return Optional.of(new OrderItem(
                              rs.getInt("order_id"),
                              rs.getInt("product_id"),
                              rs.getInt("quantity")));
                    } return Optional.empty();
                }
            }
        }
    }

    public List <OrderItem> findAll () throws SQLException {
        List <OrderItem> orderItemsList = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT order_id, product_id, quantity FROM Order_Items ORDER BY order_id, product_id")) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        orderItemsList.add(new OrderItem(
                                rs.getInt("order_id"),
                                rs.getInt("product_id"),
                                rs.getInt("quantity")));
                    }
                }
            }
        }
        return orderItemsList;
    }

    public int addOrderItem (OrderItem orderItem, Connection c) throws SQLException {
        int insertedRows;
        try (PreparedStatement preparedStatement = c.prepareStatement("INSERT INTO Order_Items (order_id, product_id, quantity) " +
                    "VALUES (?, ?, ?)")) {
                preparedStatement.setInt(1, orderItem.getOrderId());
                preparedStatement.setInt(2, orderItem.getProductId());
                preparedStatement.setInt(3, orderItem.getQuantity());
                insertedRows = preparedStatement.executeUpdate();
        }
        return insertedRows;
    }

    public int updateOrderItem (OrderItem orderItem, Connection c) throws SQLException {
        int updatedRows;
        try (PreparedStatement preparedStatement = c.prepareStatement("UPDATE Order_Items SET quantity = ? " +
                    "WHERE order_id = ? AND product_id = ?")) {
                preparedStatement.setInt(1, orderItem.getQuantity());
                preparedStatement.setInt(2, orderItem.getOrderId());
                preparedStatement.setInt(3, orderItem.getProductId());
                updatedRows = preparedStatement.executeUpdate();
        }
        return updatedRows;
    }

}
