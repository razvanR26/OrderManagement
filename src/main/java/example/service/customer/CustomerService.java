package example.service.customer;

import example.dao.CustomerDao;
import example.model.Customer;
import example.service.common.BlankValueException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService (CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void addCustomer (Customer customer) throws SQLException {
        Optional <Customer> optional;
        optional = customerDao.findById(customer.getId());
        if (optional.isPresent()) {
            throw new CustomerAlreadyExistsException("A customer with that id already exists");
        }
        validateCustomer(customer);
        customerDao.addCustomer(customer);
    }

    public void updateCustomer (Customer existingCustomer, Map <CustomerAttributes, String> updates) throws SQLException {
        if (updates.isEmpty()) {
            return;
        }
        int customerId = existingCustomer.getId();
        String lname = existingCustomer.getLname();
        String fname = existingCustomer.getFname();
        String email = existingCustomer.getEmail();
        if (updates.containsKey(CustomerAttributes.LNAME)) {
            lname = updates.get(CustomerAttributes.LNAME);
        }
        if (updates.containsKey(CustomerAttributes.FNAME)) {
            fname = updates.get(CustomerAttributes.FNAME);
        }
        if (updates.containsKey(CustomerAttributes.EMAIL)) {
            email = updates.get(CustomerAttributes.EMAIL);
        }
        Customer customer = new Customer(customerId, lname, fname, email);
        validateCustomer(customer);
        customerDao.updateCustomer(customer);
    }

    public Customer customerExists (int customerId) throws SQLException {
        Optional <Customer> existingCustomer;
        existingCustomer = customerDao.findById(customerId);
        if (existingCustomer.isEmpty()) {
            throw new CustomerNotFoundException("A customer with that id does not exist");
        }
        return existingCustomer.get();
    }

    public List <Customer> showAllCustomers () throws SQLException {
        return customerDao.findAll();
    }

    public void validateId (int id) throws SQLException {
        Optional <Customer> optional;
        optional = customerDao.findById(id);
        if (optional.isPresent()) {
            throw new CustomerAlreadyExistsException("A customer with that id already exists");
        }
    }

    public void validateName (String name) {
        if (name.isBlank()) {
            throw new BlankValueException("Customer name cannot be blank");
        }
        if (!name.matches("[\\p{L}]+([ -][\\p{L}]+)*")) {
            throw new InvalidCustomerNameFormatException("Customer name does not respect the format");
        }
    }

    public void validateEmail (String email) {
        if (email.isBlank()) {
            throw new BlankValueException("Email cannot be blank");
        }
        if (!email.matches("[A-Za-z\\d._-]+@[A-Za-z\\d]([A-Za-z\\d-]*[A-Za-z\\d])?" +
                "(\\.[A-Za-z\\d]([A-Za-z\\d-]*[A-Za-z\\d])?)*\\.[A-Za-z]{2,}")) {
            throw new InvalidEmailFormatException("Email does not respect the format");
        }
    }

    private void validateCustomer (Customer customer) {
        if (customer.getLname().isBlank()) {
            throw new BlankValueException("Last name cannot be blank");
        }
        if (customer.getFname().isBlank()) {
            throw new BlankValueException("First name cannot be blank");
        }
        if (customer.getEmail().isBlank()) {
            throw new BlankValueException("Email cannot be blank");
        }
        if (!customer.getLname().matches("[\\p{L}]+([ -][\\p{L}]+)*")) {
            throw new InvalidCustomerNameFormatException("Last name does not respect the format");
        }
        if (!customer.getFname().matches("[\\p{L}]+([ -][\\p{L}]+)*")) {
            throw new InvalidCustomerNameFormatException("First name does not respect the format");
        }
        if (!customer.getEmail().matches("[A-Za-z\\d._-]+@[A-Za-z\\d]([A-Za-z\\d-]*[A-Za-z\\d])?" +
                "(\\.[A-Za-z\\d]([A-Za-z\\d-]*[A-Za-z\\d])?)*\\.[A-Za-z]{2,}")) {
            throw new InvalidEmailFormatException("Email does not respect the format");
        }
    }
}
