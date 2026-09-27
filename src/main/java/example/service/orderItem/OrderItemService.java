package example.service.orderItem;

import example.dao.OrderDao;
import example.dao.OrderItemDao;
import example.dao.ProductDao;
import example.model.Order;
import example.model.OrderItem;
import example.model.Product;
import example.service.order.OrderNotFoundException;
import example.service.order.StockLessThanQuantityException;
import example.service.product.ProductNotFoundException;
import example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderItemService {

    private final OrderItemDao orderItemDao;
    private final OrderDao orderDao;
    private final ProductDao productDao;

    public OrderItemService (OrderItemDao orderItemDao, OrderDao orderDao, ProductDao productDao) {
        this.orderItemDao = orderItemDao;
        this.orderDao = orderDao;
        this.productDao = productDao;
    }

    public void addOrderItem (OrderItem orderItem) throws SQLException {
        Optional <OrderItem> existingOrderItem;
        existingOrderItem = orderItemDao.findById(orderItem.getOrderId(), orderItem.getProductId());
        if (existingOrderItem.isPresent()) {
            throw new OrderItemAlreadyExistsException("An orderItem with that orderId and productId already exists");
        }
        validateOrderItem(orderItem);
        Optional <Product> existingProduct;
        existingProduct = productDao.findById(orderItem.getProductId());
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException("A product with that id does not exist");
        }
        try (Connection c = DatabaseConnection.getConnection()) {
            try {
                c.setAutoCommit(false);
                int orderItemRes = orderItemDao.addOrderItem(orderItem, c);
                int productStockRes = productDao.updateProductStock(existingProduct.get().getId(), orderItem.getQuantity(), c);
                if (orderItemRes == 1 && productStockRes == 1) {
                    c.commit();
                } else {
                    OrderItemCreationFailedException creationException = new OrderItemCreationFailedException("One of the 2 operations did not affect the expected number of rows " +
                            "and the transaction was canceled");
                    try {
                        c.rollback();
                    } catch (SQLException e) {
                        creationException.addSuppressed(e);
                    }
                    throw creationException;
                }
            } catch (SQLException primaryE) {
                try {
                    c.rollback();
                } catch (SQLException secondaryE) {
                    primaryE.addSuppressed(secondaryE);
                }
                throw primaryE;
            }
        }
    }

    public void updateOrderItem (OrderItem orderItem) throws SQLException {
        Optional <OrderItem> existingOrderItem;
        existingOrderItem = orderItemDao.findById(orderItem.getOrderId(), orderItem.getProductId());
        if (existingOrderItem.isEmpty()) {
            throw new OrderItemNotFoundException("An orderItem with that orderId and productId does not exist");
        }
        Optional <Product> existingProduct;
        existingProduct = productDao.findById(orderItem.getProductId());
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException("A product with that id does not exist");
        }
        if (orderItem.getQuantity() <= 0) {
            throw new NegativeOrZeroQuantityException("The quantity cannot be negative or zero");
        }
        int oldQuantity = existingOrderItem.get().getQuantity();
        int newQuantity = orderItem.getQuantity();
        int difference;
        int updatedStock;
        int updatedOrderItem;
        if (newQuantity == oldQuantity) {
            System.out.println("The new and old quantity are equal");
            return;
        }
        try (Connection c = DatabaseConnection.getConnection()) {
            UpdateOrderItemFailedException updateFailed = new UpdateOrderItemFailedException("One of the 2 operations did not affect the expected number of rows " +
                    "and the transaction was canceled");
            try {
                c.setAutoCommit(false);
                if (newQuantity > oldQuantity) {
                    difference = newQuantity - oldQuantity;
                    if (existingProduct.get().getStock() < difference) {
                        throw new StockLessThanQuantityException("The new quantity cannot be greater than the stock of that product");
                    }
                    updatedStock = productDao.updateProductStock(orderItem.getProductId(), difference, c);
                    if (updatedStock == 0) {
                        throw updateFailed;
                    }
                    updatedOrderItem = orderItemDao.updateOrderItem(orderItem, c);
                    if (updatedOrderItem == 0) {
                        throw updateFailed;
                    }
                } else {
                    difference = oldQuantity - newQuantity;
                    updatedStock = productDao.restoreProductStock(orderItem.getProductId(), difference, c);
                    if (updatedStock == 0) {
                        throw updateFailed;
                    }
                    updatedOrderItem = orderItemDao.updateOrderItem(orderItem, c);
                    if (updatedOrderItem == 0) {
                        throw updateFailed;
                    }
                }
                    c.commit();
            } catch (SQLException | UpdateOrderItemFailedException primaryE) {
                try {
                    c.rollback();
                } catch (SQLException secondaryE) {
                    primaryE.addSuppressed(secondaryE);
                }
                throw primaryE;
            }
        }
    }

    public List <OrderItem> showAllOrderItem () throws SQLException {
        return orderItemDao.findAll();
    }

    public void validateOrderExists(int orderId) throws SQLException {
        Optional <Order> existingOrder;
        existingOrder = orderDao.findById(orderId);
        if (existingOrder.isEmpty()) {
            throw new OrderNotFoundException("An order with that id does not exist");
        }
    }

    public void validateProductExists(int productId) throws SQLException {
        Optional <Product> existingProduct;
        existingProduct = productDao.findById(productId);
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException("A product with that id does not exist");
        }
    }

    public void validateOrderItemExists(int orderId, int productId) throws SQLException {
        Optional <OrderItem> existingOrderItem;
        existingOrderItem = orderItemDao.findById(orderId, productId);
        if (existingOrderItem.isEmpty()) {
            throw new OrderItemNotFoundException("An orderItem with that orderId and productId does not exist");
        }
    }

    private void validateOrderItem (OrderItem orderItem) throws SQLException {
        Optional <Order> existingOrder;
        existingOrder = orderDao.findById(orderItem.getOrderId());
        Optional <Product> existingProduct;
        existingProduct = productDao.findById(orderItem.getProductId());
        if (existingOrder.isEmpty()) {
            throw new OrderNotFoundException("An order with that id does not exist");
        }
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException("A product with that id does not exist");
        }
        if (orderItem.getQuantity() <= 0) {
            throw new NegativeOrZeroQuantityException("The quantity cannot be negative or zero");
        }
        if (existingProduct.get().getStock() < orderItem.getQuantity()) {
            throw new StockLessThanQuantityException("The quantity cannot be greater than the stock of that product");
        }
    }
}
