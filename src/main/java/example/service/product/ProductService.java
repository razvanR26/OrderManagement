package example.service.product;

import example.dao.ProductDao;
import example.model.Product;
import example.service.common.BlankValueException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductService {

    private final ProductDao productDao;

    public ProductService (ProductDao productDao) {
        this.productDao = productDao;
    }

    public void addProduct (Product product) throws SQLException {
        Optional <Product> optional;
        optional = productDao.findById(product.getId());
        if (optional.isPresent()) {
            throw new ProductAlreadyExistsException("A product with that id already exists");
        }
        validateProduct(product);
        productDao.addProduct(product);
    }

    public void updateProduct (Product existingProduct, Map <ProductAttributes, Object> updates) throws SQLException {
        if (updates.isEmpty()) {
            return;
        }
        int id = existingProduct.getId();
        String name = existingProduct.getName();
        BigDecimal price = existingProduct.getPrice();
        int stock = existingProduct.getStock();
        if (updates.containsKey(ProductAttributes.NAME)) {
            name = (String) updates.get(ProductAttributes.NAME);
        }
        if (updates.containsKey(ProductAttributes.PRICE)) {
            price = (BigDecimal) updates.get(ProductAttributes.PRICE);
        }
        if (updates.containsKey(ProductAttributes.STOCK)) {
            stock = (Integer) updates.get(ProductAttributes.STOCK);
        }
        Product product = new Product (id, name, price, stock);
        validateProduct(product);
        productDao.updateProduct(product);
    }

    public Product productExists (int productId) throws SQLException {
        Optional <Product> existingProduct;
        existingProduct = productDao.findById(productId);
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException("A product with that id does not exist");
        }
        return existingProduct.get();
    }

    public List <Product> showAllProducts () throws SQLException {
        return productDao.findAll();
    }

    public void validateId (int id) throws SQLException {
        Optional <Product> optional;
        optional = productDao.findById(id);
        if (optional.isPresent()) {
            throw new ProductAlreadyExistsException("A product with that id already exists");
        }
    }

    public void validateName (String name) {
        if (name.isBlank()) {
            throw new BlankValueException("Product name cannot be blank");
        }
        if (!name.matches(".*\\p{L}.*")) {
            throw new InvalidProductNameFormatException("Product name does not respect the format");
        }
        if (!name.matches("[\\p{L}\\d][\\p{L}\\d .()-]*[\\p{L}\\d]")) {
            throw new InvalidProductNameFormatException("Product name does not respect the format");
        }
    }

    public void validatePrice (BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PriceNegativeOrZeroException("The introduced price is negative or zero");
        }
    }

    public void validateStock (int stock) {
        if (stock < 0) {
            throw new StockNegativeException("The introduced stock is negative");
        }
    }

    private void validateProduct (Product product) {
        if (product.getName().isBlank()) {
            throw new BlankValueException("Product name cannot be blank");
        }
        if (!product.getName().matches(".*\\p{L}.*")) {
            throw new InvalidProductNameFormatException("Product name does not respect the format");
        }
        if (!product.getName().matches("[\\p{L}\\d][\\p{L}\\d .()-]*[\\p{L}\\d]")) {
            throw new InvalidProductNameFormatException("Product name does not respect the format");
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PriceNegativeOrZeroException("The introduced price is negative or zero");
        }
        if (product.getStock() < 0) {
            throw new StockNegativeException("The introduced stock is negative");
        }
    }

}
