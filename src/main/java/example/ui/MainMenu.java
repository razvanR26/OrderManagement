package example.ui;

import example.dao.CustomerDao;
import example.dao.OrderDao;
import example.dao.OrderItemDao;
import example.dao.ProductDao;
import example.service.customer.CustomerService;
import example.service.order.OrderService;
import example.service.orderItem.OrderItemService;
import example.service.product.ProductService;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu {

    private final CustomerMenu customerMenu;
    private final ProductMenu productMenu;
    private final OrderMenu orderMenu;
    private final OrderItemMenu orderItemMenu;
    private final Scanner scanner = new Scanner(System.in);

    public MainMenu (CustomerDao customerDao, ProductDao productDao, OrderDao orderDao, OrderItemDao orderItemDao) {
        customerMenu = new CustomerMenu(new CustomerService(customerDao), scanner);
        productMenu = new ProductMenu(new ProductService(productDao), scanner);
        orderMenu = new OrderMenu(new OrderService(orderDao, customerDao, orderItemDao, productDao), scanner);
        orderItemMenu = new OrderItemMenu(new OrderItemService(orderItemDao, orderDao, productDao), scanner);
    }

    public void start () {
        boolean running = true;
        while (running) {
            System.out.println("Welcome to the order management app main menu");
            System.out.println("1 - CustomerMenu");
            System.out.println("2 - ProductMenu");
            System.out.println("3 - OrderMenu");
            System.out.println("4 - OrderItemMenu");
            System.out.println("0 - Exit the app");
            System.out.println();
            System.out.println("Enter the option:");
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
            case 1 -> customerMenu.start();
            case 2 -> productMenu.start();
            case 3 -> orderMenu.start();
            case 4 -> orderItemMenu.start();
            case 0 -> {
                System.out.println("You have closed the app");
                return false;
            }
            default -> System.out.println("Invalid option: " + option + ". Enter a valid option");
        }
        return true;
    }
}
