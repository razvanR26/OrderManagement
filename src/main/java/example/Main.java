package example;

import example.dao.CustomerDao;
import example.dao.OrderDao;
import example.dao.OrderItemDao;
import example.dao.ProductDao;
import example.ui.MainMenu;



public class Main {
    public static void main(String[] args) {
        CustomerDao customerDao = new CustomerDao();
        ProductDao productDao = new ProductDao();
        OrderDao orderDao = new OrderDao();
        OrderItemDao orderItemDao = new OrderItemDao();
        MainMenu mainMenu = new MainMenu(customerDao, productDao, orderDao, orderItemDao);
        mainMenu.start();
    }
}
