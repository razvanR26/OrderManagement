package example.ui;

import example.model.OrderItem;
import example.service.orderItem.OrderItemOperationEnum;
import example.service.orderItem.OrderItemService;
import example.ui.common.Prompter;
import example.ui.common.Reader;
import static example.ui.common.Helper.read;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class OrderItemMenu {

    private final OrderItemService orderItemService;
    private final Scanner scanner;

    public OrderItemMenu (OrderItemService orderItemService, Scanner scanner) {
        this.orderItemService = orderItemService;
        this.scanner = scanner;
    }

    public void start () {
        boolean running = true;
        while (running) {
            System.out.println("Welcome to the orderItem menu");
            System.out.println("1 - add item to order");
            System.out.println("2 - update item in order");
            System.out.println("3 - list all orderItems");
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
            case 1 -> addItemToOrder();
            case 2 -> updateItem();
            case 3 -> listOrderItemMenu();
            case 0 -> {
                System.out.println("You have returned to the main menu");
                return false;
            }
            default -> System.out.println("Invalid option: " + option + ". Enter a valid option");
        }
        return true;
    }

    private void addItemToOrder () {
        Prompter orderIdPrompter = () -> System.out.println("Enter an orderId that exists");
        Prompter productIdPrompter = () -> System.out.println("Enter a productId that exists");
        Prompter quantityPrompter = () -> System.out.println("Enter the quantity");
        try {
            OrderItem orderItem = readOrderItem(orderIdPrompter, productIdPrompter, quantityPrompter, OrderItemOperationEnum.ADD);
            orderItemService.addOrderItem(orderItem);
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateItem () {
        Prompter orderIdPrompter = () -> System.out.println("Enter an orderId that exists");
        Prompter productIdPrompter = () -> System.out.println("Enter a productId that is associated with the entered orderId");
        Prompter quantityPrompter = () -> System.out.println("Enter the new quantity");
        try {
            OrderItem orderItem = readOrderItem(orderIdPrompter, productIdPrompter, quantityPrompter, OrderItemOperationEnum.UPDATE);
            orderItemService.updateOrderItem(orderItem);
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listOrderItemMenu () {
        try {
            List <OrderItem> allOrderItem = orderItemService.showAllOrderItem();
            allOrderItem.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private OrderItem readOrderItem (Prompter orderIdPrompter, Prompter productIdPrompter, Prompter quantityPrompter, OrderItemOperationEnum operation) throws SQLException {
        Reader <Integer> integerReader = () -> {
            int integer;
            try {
                integer = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return integer;
        };
        int orderId = read(orderIdPrompter, integerReader, "orderId");
        orderItemService.validateOrderExists(orderId);
        int productId = read(productIdPrompter, integerReader, "productId");
        orderItemService.validateProductExists(productId);
        if (operation == OrderItemOperationEnum.UPDATE) {
            orderItemService.validateOrderItemExists(orderId, productId);
        }
        int quantity = read(quantityPrompter, integerReader, "quantity");

        return new OrderItem(orderId, productId, quantity);
    }
}
