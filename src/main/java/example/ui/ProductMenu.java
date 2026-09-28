package example.ui;

import example.model.Product;
import example.service.product.ProductAttributes;
import example.service.product.ProductService;
import example.ui.common.Prompter;
import example.ui.common.Reader;
import example.ui.common.Validator;
import static example.ui.common.Helper.helpValidate;
import static example.ui.common.Helper.read;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class ProductMenu {

    private final ProductService productService;
    private final Scanner scanner;

    public ProductMenu (ProductService productService, Scanner scanner) {
        this.productService = productService;
        this.scanner = scanner;
    }

    public void start () {
        boolean running = true;
        while (running) {
            System.out.println("Welcome to the product menu");
            System.out.println("1 - add product");
            System.out.println("2 - update product");
            System.out.println("3 - find product");
            System.out.println("4 - list products");
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
            case 1 -> addProductMenu();
            case 2 -> updateProductMenu();
            case 3 -> findProductMenu();
            case 4 -> listProductsMenu();
            case 0 -> {
                System.out.println("You have returned to the main menu");
                return false;
            }
            default -> System.out.println("Invalid option: " + option + ". Enter a valid option");
        }
        return true;
    }

    private void addProductMenu () {
        Prompter idPrompter = () -> System.out.println("Set an id for the new product:");
        Prompter namePrompter = () -> System.out.println("Set a name for the new product:");
        Prompter pricePrompter = () -> System.out.println("Set a price for the new product:");
        Prompter stockPrompter = () -> System.out.println("Set a stock for the new product:");
        Reader <String> stringReader = scanner::nextLine;
        Reader <Integer> integerReader = () -> {
            int integer;
            try {
                integer = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return integer;
        };
        Reader <BigDecimal> priceReader = () -> {
            BigDecimal price;
            try {
                price = scanner.nextBigDecimal();
            } finally {
                scanner.nextLine();
            }
            return price;
        };
        Validator <String> nameValidator = productService::validateName;
        Validator <Integer> idValidator = productService::validateId;
        Validator <BigDecimal> priceValidator = productService::validatePrice;
        Validator <Integer> stockValidator = productService::validateStock;

        try {
            int id = helpValidate(idPrompter, integerReader, idValidator, "id");
            String name = helpValidate(namePrompter, stringReader, nameValidator, "name");
            BigDecimal price = helpValidate(pricePrompter, priceReader, priceValidator, "price");
            int stock = helpValidate(stockPrompter, integerReader, stockValidator, "stock");

            Product product = new Product(id, name, price, stock);
            productService.addProduct(product);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateProductMenu () {
        Prompter idPrompter = () -> System.out.println("Enter an id of an existing product:");
        Prompter updatePrompter = () -> System.out.println("Enter your option:");
        Prompter namePrompter = () -> System.out.println("Enter a new name:");
        Prompter pricePrompter = () -> System.out.println("Enter a new price:");
        Prompter stockPrompter = () -> System.out.println("Enter a new stock:");
        Reader <String> stringReader = scanner::nextLine;
        Reader <Integer> integerReader = () -> {
            int integer;
            try {
                integer = scanner.nextInt();
            } finally {
                scanner.nextLine();
            }
            return integer;
        };
        Reader <BigDecimal> priceReader = () -> {
            BigDecimal price;
            try {
                price = scanner.nextBigDecimal();
            } finally {
                scanner.nextLine();
            }
            return price;
        };
        Validator <String> nameValidator = productService::validateName;
        Validator <BigDecimal> priceValidator = productService::validatePrice;
        Validator <Integer> stockValidator = productService::validateStock;
        Map <ProductAttributes, Object> updates = new HashMap<>();
        int productId = read(idPrompter, integerReader, "id");
        ProductAttributes productAttributes;
        boolean running = true;
        try {
            Product existingProduct = productService.productExists(productId);
            while (running) {

                System.out.println("Select what you want to update:");
                System.out.println("1 - name");
                System.out.println("2 - price");
                System.out.println("3 - stock");
                System.out.println("0 - finish");
                int option = read(updatePrompter, integerReader, "option");
                switch (option) {
                    case 1 -> productAttributes = ProductAttributes.NAME;
                    case 2 -> productAttributes = ProductAttributes.PRICE;
                    case 3 -> productAttributes = ProductAttributes.STOCK;
                    case 0 -> {
                        System.out.println("You have exited the updateProduct menu");
                        running = false;
                        continue;
                    }
                    default -> {
                        System.out.println("Invalid option: " + option + ". Enter a new one");
                        continue;
                    }
                }
                if (updates.containsKey(productAttributes)) {
                    System.out.println("You have already changed this attribute");
                    continue;
                }
                Object newValue = null;
                switch (option) {
                    case 1 -> newValue = helpValidate(namePrompter, stringReader, nameValidator, "name");
                    case 2 -> newValue = helpValidate(pricePrompter, priceReader, priceValidator, "price");
                    case 3 -> newValue = helpValidate(stockPrompter, integerReader, stockValidator, "stock");
                }
                updates.put(productAttributes, newValue);
            }
            productService.updateProduct(existingProduct, updates);
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findProductMenu () {
        Prompter idPrompter = () -> System.out.println("Enter the id of the product you want to find:");
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
            Product product = productService.productExists(id);
            System.out.println("Product found:");
            System.out.println("id: " + product.getId());
            System.out.println("name: " + product.getName());
            System.out.println("price: " + product.getPrice());
            System.out.println("stock: " + product.getStock());
            System.out.println();
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listProductsMenu () {
        try {
            List <Product> allProducts = productService.showAllProducts();
            allProducts.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
