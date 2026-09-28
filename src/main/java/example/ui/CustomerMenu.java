package example.ui;

import example.model.Customer;
import example.service.customer.CustomerAttributes;
import example.service.customer.CustomerService;
import example.ui.common.Prompter;
import example.ui.common.Reader;
import example.ui.common.Validator;

import java.sql.SQLException;
import java.util.*;

import static example.ui.common.Helper.helpValidate;
import static example.ui.common.Helper.read;

public class CustomerMenu {

    private final CustomerService customerService;
    private final Scanner scanner;

    public CustomerMenu (CustomerService customerService, Scanner scanner) {
        this.customerService = customerService;
        this.scanner = scanner;
    }

    public void start () {
        boolean running = true;
        while (running) {
            System.out.println("Welcome to the customer menu");
            System.out.println("1 - add customer");
            System.out.println("2 - update customer");
            System.out.println("3 - find customer");
            System.out.println("4 - list customers");
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
            case 1 -> addCustomerMenu();
            case 2 -> updateCustomerMenu();
            case 3 -> findCustomerMenu();
            case 4 -> listCustomersMenu();
            case 0 -> {
                System.out.println("You have returned to the main menu");
                return false;
            }
            default -> System.out.println("Invalid option: " + option + ". Enter a valid option");
        }
        return true;
    }

    private void addCustomerMenu () {
        Prompter idPrompter = () -> System.out.println("Set an id for the new customer:");
        Prompter lNamePrompter = () -> System.out.println("Set a lname for the new customer:");
        Prompter fNamePrompter = () -> System.out.println("Set a fname for the new customer:");
        Prompter emailPrompter = () -> System.out.println("Set an email for the new customer:");
        Reader <String> stringReader = scanner::nextLine;
        Validator <String> nameValidator = customerService::validateName;
        Validator <String> emailValidator = customerService::validateEmail;
        Reader <Integer> integerReader = () -> {
            int option;
            try {
                option = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return option;
        };
        Validator <Integer> integerValidator = customerService::validateId;
        try {
                int id = helpValidate(idPrompter, integerReader, integerValidator, "id");
                String lname = helpValidate(lNamePrompter, stringReader, nameValidator, "lname");
                String fname = helpValidate(fNamePrompter, stringReader, nameValidator, "fname");
                String email = helpValidate(emailPrompter, stringReader, emailValidator, "email");

                Customer customer = new Customer(id, lname, fname, email);
                customerService.addCustomer(customer);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
        }
    }

    private void updateCustomerMenu () {
        Reader <String> stringReader = scanner::nextLine;
        Validator <String> nameValidator = customerService::validateName;
        Validator <String> emailValidator = customerService::validateEmail;
        Reader <Integer> integerReader = () -> {
            int integer;
            try {
                integer = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return integer;
        };
        Prompter idPrompter = () -> System.out.println("Enter an id of an existing customer");
        Prompter updatePrompter = () -> System.out.println("Enter your option:");
        Prompter lnamePrompter = () -> System.out.println("Enter a new lname");
        Prompter fnamePrompter = () -> System.out.println("Enter a new fname");
        Prompter emailPrompter = () -> System.out.println("Enter a new email");
        Map <CustomerAttributes, String> updates = new HashMap<>();
        int customerId = read(idPrompter, integerReader, "id");
        CustomerAttributes customerAttributes;
        boolean running = true;
        try {
            Customer existingCustomer = customerService.customerExists(customerId);
            while (running) {

                System.out.println("Select what you want to update:");
                System.out.println("1 - lname");
                System.out.println("2 - fname");
                System.out.println("3 - email");
                System.out.println("0 - finish");
                int option = read(updatePrompter, integerReader, "option");
                switch (option) {
                    case 1 -> customerAttributes = CustomerAttributes.LNAME;
                    case 2 -> customerAttributes = CustomerAttributes.FNAME;
                    case 3 -> customerAttributes = CustomerAttributes.EMAIL;
                    case 0 -> {
                        System.out.println("You have exited the updateCustomer menu");
                        running = false;
                        continue;
                    }
                    default -> {
                        System.out.println("Invalid option: " + option + " Enter a new one");
                        continue;
                    }
                }
                if (updates.containsKey(customerAttributes)) {
                    System.out.println("You have already changed this attribute");
                    continue;
                }
                String newValue = null;
                switch (option) {
                    case 1 -> newValue = helpValidate(lnamePrompter, stringReader, nameValidator, "lname");
                    case 2 -> newValue = helpValidate(fnamePrompter, stringReader, nameValidator, "fname");
                    case 3 -> newValue = helpValidate(emailPrompter, stringReader, emailValidator, "email");
                }
                updates.put(customerAttributes, newValue);
            }
            customerService.updateCustomer(existingCustomer, updates);
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findCustomerMenu () {
        Prompter idPrompter = () -> System.out.println("Enter the id of the customer you want to find:");
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
            Customer customer = customerService.customerExists(id);
            System.out.println("Customer found:");
            System.out.println("id: " + customer.getId());
            System.out.println("lname: " + customer.getLname());
            System.out.println("fname: " + customer.getFname());
            System.out.println("email: " + customer.getEmail());
            System.out.println();
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listCustomersMenu () {
        try {
            List <Customer> allCustomers = customerService.showAllCustomers();
            allCustomers.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
