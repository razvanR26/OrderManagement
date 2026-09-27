package example.dao;

import example.model.Product;
import example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDao {

    public Optional <Product> findById (int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT id, name, price, stock " +
                    "FROM products WHERE id = ?")) {
                preparedStatement.setInt(1, id);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getBigDecimal("price"),
                                rs.getInt("stock")
                        ));
                    } else return Optional.empty();
                }
            }
        }
    }

    public List <Product> findAll () throws SQLException {
        List <Product> productsList = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("SELECT id, name, price, stock FROM products ORDER BY id")) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        productsList.add(new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getBigDecimal("price"),
                                rs.getInt("stock")));
                    }
                }
            }
        }
        return productsList;
    }

    public int addProduct (Product product) throws SQLException {
        int insertedRows;
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("INSERT INTO products (id, name, price, stock) " +
                    "VALUES (?, ?, ?, ?)")) {
                preparedStatement.setInt(1, product.getId());
                preparedStatement.setString(2, product.getName());
                preparedStatement.setBigDecimal(3, product.getPrice());
                preparedStatement.setInt(4, product.getStock());
                insertedRows = preparedStatement.executeUpdate();
            }
        }
        return insertedRows;
    }

    public int updateProductStock (int productId, int quantity, Connection c) throws SQLException {
        int updatedRows;
        try (PreparedStatement preparedStatement = c.prepareStatement("UPDATE products SET stock = stock - ? " +
                "WHERE id = ? AND stock >= ?")) {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantity);
            updatedRows = preparedStatement.executeUpdate();
        }
        return updatedRows;
    }

    public int restoreProductStock (int productId, int quantity, Connection c) throws SQLException {
        int updatedRows;
        try (PreparedStatement preparedStatement = c.prepareStatement("UPDATE products SET stock = stock + ? " +
                "WHERE id = ?")) {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productId);
            updatedRows = preparedStatement.executeUpdate();
        }
        return updatedRows;
    }

    public int updateProduct (Product product) throws SQLException {
        int updatedRows;
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = c.prepareStatement("UPDATE products SET name = ?, price = ?, stock = ? " +
                    "WHERE id = ?")) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setBigDecimal(2, product.getPrice());
                preparedStatement.setInt(3, product.getStock());
                preparedStatement.setInt(4, product.getId());
                updatedRows = preparedStatement.executeUpdate();
            }
        }
        return updatedRows;
    }

}
