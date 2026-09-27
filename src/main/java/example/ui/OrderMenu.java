package example.ui;

import example.model.Order;
import example.model.OrderItem;
import example.model.Product;
import example.service.order.OrderService;
import example.ui.common.Prompter;
import example.ui.common.Reader;
import example.ui.common.Validator;

import static example.ui.common.Helper.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class OrderMenu {

    private final OrderService orderService;
    private final Scanner scanner = new Scanner(System.in);

    public OrderMenu (OrderService orderService) {
        this.orderService = orderService;
    }

    public void start () {
        boolean running = true;
        while (running) {
            System.out.println("Welcome to the order menu");
            System.out.println("1 - create order");
            System.out.println("2 - update order");
            System.out.println("3 - find order");
            System.out.println("4 - list orders");
            System.out.println("0 - back to main menu");
            System.out.println();
            System.out.println("Enter your option:");
            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                running = validateOption(option);
            } catch (InputMismatchException e) {
                System.out.println("Enter a valid number for the menu");
                scanner.nextLine();
            }
        }
    }

    private boolean validateOption (int option) {
        switch (option) {
            case 1 -> createOrderMenu();
            case 2 -> updateOrderMenu();
            case 3 -> findOrderMenu();
            case 4 -> listOrdersMenu();
            case 0 -> {
                System.out.println("You have returned to the main menu");
                return false;
            }
            default -> System.out.println("Invalid option: " + option + ". Enter a valid option");
        }
        return true;
    }

    private void createOrderMenu () {
        Prompter orderIdPrompter = () -> System.out.println("Enter a new orderId:");
        Prompter customerIdPrompter = () -> System.out.println("Enter an existing customerId:");
        Prompter productIdPrompter = () -> System.out.println("Enter an existing productId:");
        Prompter quantityPrompter = () -> System.out.println("Enter a quantity:");
        Reader <Integer> integerReader = () -> {
            int integer;
            try {
                integer = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return integer;
        };
        Validator <Integer> orderIdValidator = orderService::validateCreateOrder;
        Validator <Integer> customerIdValidator = orderService::validateCustomerId;

        try {
            int orderId = helpValidate(orderIdPrompter, integerReader, orderIdValidator, "orderId");
            int customerId = helpValidate(customerIdPrompter, integerReader, customerIdValidator, "customerId");
            int productId = read(productIdPrompter, integerReader, "productId");
            Product product = orderService.validateProductId(productId);
            int quantity = read(quantityPrompter, integerReader, "quantity");
            orderService.validateQuantity(product, quantity);
            Order order = new Order(orderId, customerId, LocalDateTime.now());
            OrderItem orderItem = new OrderItem(orderId, productId, quantity);
            orderService.createOrder(order, orderItem);
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateOrderMenu () {
        Prompter idPrompter = () -> System.out.println("Enter an id of an existing order:");
        Prompter customerIdPrompter = () -> System.out.println("Enter a new customerId:");
        Reader <Integer> integerReader = () -> {
            int integer;
            try {
                integer = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return integer;
        };
        Validator <Integer> customerIdValidator = orderService::validateCustomerId;
        int orderId = read(idPrompter, integerReader, "id");
        try {
            Order existingOrder = orderService.orderExists(orderId);
            int customerId = helpValidate(customerIdPrompter, integerReader, customerIdValidator, "customerId");
            Order updatedOrder = new Order(existingOrder.getId(), customerId, existingOrder.getDate());
            orderService.updateOrder(updatedOrder);
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findOrderMenu () {
        Prompter idPrompter = () -> System.out.println("Enter the id of the order you want to find");
        Reader <Integer> idReader = () -> {
            int id;
            try {
                id = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return id;
        };
        try {
            int id = read(idPrompter, idReader, "id");
            Order order = orderService.orderExists(id);
            System.out.println("Order found:");
            System.out.println("id: " + order.getId());
            System.out.println("customerId: " + order.getCustomerId());
            System.out.println("date: " + order.getDate());
            System.out.println();
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listOrdersMenu () {
        try {
            List <Order> allOrders = orderService.showAllOrders();
            allOrders.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
