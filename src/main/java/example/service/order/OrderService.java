package example.service.order;

import example.dao.CustomerDao;
import example.dao.OrderDao;
import example.dao.OrderItemDao;
import example.dao.ProductDao;
import example.model.Customer;
import example.model.Order;
import example.model.OrderItem;
import example.model.Product;
import example.service.customer.CustomerNotFoundException;
import example.service.orderItem.NegativeOrZeroQuantityException;
import example.service.product.ProductNotFoundException;
import example.util.DatabaseConnection;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;

public class OrderService {

    private final OrderDao orderDao;
    private final CustomerDao customerDao;
    private final OrderItemDao orderItemDao;
    private final ProductDao productDao;

    public OrderService (OrderDao orderDao, CustomerDao customerDao, OrderItemDao orderItemDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.customerDao = customerDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
    }

    public void createOrder (Order order, OrderItem orderItem) throws SQLException {
        Optional <Order> existingOrder;
        existingOrder = orderDao.findById(order.getId());
        if (existingOrder.isPresent()) {
            throw new OrderAlreadyExistsException("An order with that id already exists");
        }
        validateOrder(order);
        if (order.getId() != orderItem.getOrderId()) {
            throw new OrderIdMismatchException("The order id from the order does not match the order id from the orderItem");
        }
        Optional <Product> existingProduct;
        existingProduct = productDao.findById(orderItem.getProductId());
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException("A product with that id does not exist");
        }
        if (orderItem.getQuantity() <= 0) {
            throw new NegativeOrZeroQuantityException("The quantity cannot be negative or zero");
        }
        if (existingProduct.get().getStock() < orderItem.getQuantity()) {
            throw new StockLessThanQuantityException("The stock have a lower value than the quantity ordered");
        }
        try (Connection c = DatabaseConnection.getConnection()) {
            try {
                c.setAutoCommit(false);
                int orderRes = orderDao.addOrder(order, c);
                int orderItemRes = orderItemDao.addOrderItem(orderItem, c);
                int productRes = productDao.updateProductStock(existingProduct.get().getId(), orderItem.getQuantity(), c);
                if (orderRes == 1 && orderItemRes == 1 && productRes == 1) {
                    c.commit();
                } else {
                    OrderCreationFailedException creationException =
                            new OrderCreationFailedException("One of the 3 operations did not affect the expected number of rows " +
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

    public void updateOrder (Order order) throws SQLException {
        validateOrder(order);
        orderDao.updateOrder(order);
    }

    public Order orderExists (int orderId) throws SQLException {
        Optional <Order> existingOrder;
        existingOrder = orderDao.findById(orderId);
        if (existingOrder.isEmpty()) {
            throw new OrderNotFoundException("An order with that id does not exist");
        }
        return existingOrder.get();
    }

    public List <Order> showAllOrders () throws SQLException {
        return orderDao.findAll();
    }

    public void validateCustomerId (int customerId) throws SQLException {
        Optional <Customer> existingCustomer;
        existingCustomer = customerDao.findById(customerId);
        if (existingCustomer.isEmpty()) {
            throw new CustomerNotFoundException("A customer with that id does not exist");
        }
    }

    public void validateCreateOrder (int orderId) throws SQLException {
        Optional <Order> order;
        order = orderDao.findById(orderId);
        if (order.isPresent()) {
            throw new OrderAlreadyExistsException("An order with that id already exists");
        }
    }

    public Product validateProductId (int productId) throws SQLException {
        Optional <Product> existingProduct;
        existingProduct = productDao.findById(productId);
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException("A product with that id does not exist");
        }
        return existingProduct.get();
    }

    public void validateQuantity (Product product, int quantity) {
        if (quantity <= 0) {
            throw new NegativeOrZeroQuantityException("The quantity cannot be negative or zero");
        }
        if (product.getStock() < quantity) {
            throw new StockLessThanQuantityException("The stock have a lower value than the quantity ordered");
        }
    }

    private void validateOrder (Order order) throws SQLException {
        Optional <Customer> optional;
        optional = customerDao.findById(order.getCustomerId());
        if (optional.isEmpty()) {
            throw new CustomerNotFoundException("A customer with that id does not exist");
        }
        if (order.getDate() == null) {
            throw new NullDateException("The date cannot be null");
        }
    }
}
