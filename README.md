# Order Management🖥

This is a personal portfolio console-based Java application that manages customers, products, orders, and order items, using PostgreSQL as its database.

# Description✨

This application implements CRUD operations for customers, products, orders and order items through a well-structured architecture in which each package focuses on a specific task. It interacts with a PostgreSQL database through JDBC, using `PreparedStatement` for SQL operations. The architecture is divided into Model, DAO, Service, UI, Util and common packages. The Model package contains the attributes of the objects and the overridden `toString()` method. The DAO package handles the interaction with the database system. The Service layer contains the business logic and takes care of validating the objects before sending them to the DAO. The UI package handles user interaction through a menu-driven interface, while the common package contains functional interfaces used to reduce code repetition. The Util package contains a class that returns a connection to the database. The app treats operations that modify multiple entities at the same time as atomic, using transactions for modifications involving orders, order items and stock updates. Either all the changes are applied successfully or the transaction is rolled back. Input validation has been carefully implemented using custom exceptions that contain descriptive messages for the different validation errors that can occur.

# Features✅

- Customer Management (add customer, update customer, find customer by ID, list customers)
- Product Management (add product, update product, find product by ID, list products)
- Order Management (create order, update order, find order by ID, list orders)
- Order Item Management (add item to order, update item in order, list all order items)
- Stock Management (automatically updates stock when orders are created or order items are created or modified)

# Technologies🛠️

- Java 23
- Maven
- JDBC
- PostgreSQL
- DBeaver

# Key concepts demonstrated🔑

This application demonstrates a layered Java application with database integration, security considerations, and several software design concepts.

## OOP & Java Fundamentals

- Encapsulation
- Immutability with `final`
- Enums
- Method overriding (`toString()`)

## Collections & Generics

- Java Collections (`List`, `Map`)
- Generics
- `Optional`

## Functional Programming

- Functional Interfaces (`Prompter`, `Reader`, `Validator` for reusable input loops)
- Lambda Expressions
- Streams

## Exception & Error Handling

- Custom Exceptions
- Exception Propagation through layers (from lower layers to UI)
- `try-with-resources`
- `addSuppressed()` for preserving secondary exceptions

## JDBC & Database

- JDBC
- PostgreSQL
- `PreparedStatement`
- `ResultSet`
- Transactions (commit and rollback for order, order item and stock updates)
- `BigDecimal` for monetary values
- `LocalDateTime` for order timestamps

## Architecture & Design 

- Layered Architecture
- DAO / Service separation
- Dependency Injection (constructor injection)
- Separation of concerns
- Reusable validation with functional interfaces

## Validation & Business Logic

- Input Validation
- Business Rule Validation (customer, product, order, order item rules)
- Regular Expressions (Regex)
- Validation at UI and Service layers

## Database Security & Integrity 

- Environment Variables
- Restricted database user / permissions
- Primary keys
- Foreign keys
- Database constraints (stock >= 0, unique email, check price > 0)
- SQL injection prevention with parameterised queries

# Database Configuration💾

The application uses PostgreSQL as its relational database.
For the application to work correctly, create a dedicated database user with `SELECT`, `INSERT` and `UPDATE` permissions. The user does not require `DELETE` permission.

```text
DB_URL=your_db_url
DB_USER=your_db_user
DB_PASSWORD=your_db_password
```

```sql
CREATE TABLE Customers (
id INTEGER PRIMARY KEY, 
lname TEXT NOT NULL, 
fname TEXT NOT NULL, 
email TEXT NOT NULL UNIQUE);

CREATE TABLE Products (
id INTEGER PRIMARY KEY, 
name TEXT NOT NULL, 
price NUMERIC (10,2) CHECK (price > 0), 
stock INTEGER CHECK (stock >= 0)); 

CREATE TABLE Orders (
id INTEGER PRIMARY KEY, 
customer_id INTEGER NOT NULL REFERENCES Customers (id), 
date TIMESTAMP NOT NULL); 

CREATE TABLE Order_Items (
order_id INTEGER REFERENCES Orders (id), 
product_id INTEGER REFERENCES Products (id), 
quantity INTEGER NOT NULL CHECK (quantity > 0), 
PRIMARY KEY (order_id, product_id));
```

# Project Structure📂

```text
src/
└── main/
    └── java/
        └── example/
            ├── model/ - domain objects
            ├── dao/ - database access
            ├── service/ - business logic and validation
            ├── ui/ - menu-driven user interaction
            │   └── common/ - reusable functional interfaces for input handling
            └── util/ - database connection
```

# How to Run🚀

## Prerequisites

- JDK 23
- IntelliJ IDEA
- PostgreSQL
- DBeaver or other graphical database application

## Clone the Repository

```bash
git clone https://github.com/razvanR26/OrderManagement.git
```

## Database Setup

Follow the instructions in the **Database Configuration** section to:

- create the dedicated PostgreSQL user with the required permissions
- create the database tables
- configure the required environment variables

## Open the Project

Open the cloned `OrderManagement` folder as a Maven project in IntelliJ IDEA.

## Run the Application

Run the `Main` class from IntelliJ IDEA.
The application will start with a menu-driven interface where you can manage customers, products, orders, and order items.
