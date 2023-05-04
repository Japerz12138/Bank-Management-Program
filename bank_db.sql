CREATE DATABASE IF NOT EXISTS bank_db;

USE bank_db;

CREATE TABLE IF NOT EXISTS customers (
  customers_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  customers_name VARCHAR(255) NOT NULL,
  customers_dob DATE NOT NULL,
  customers_username VARCHAR(50) NOT NULL UNIQUE,
  customers_email VARCHAR(255) NOT NULL UNIQUE,
  customers_address VARCHAR(255) NOT NULL,
  customers_password VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS employees (
  employees_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  employees_name VARCHAR(255) NOT NULL,
  employees_dob DATE NOT NULL,
  employees_username VARCHAR(50) NOT NULL UNIQUE,
  employees_email VARCHAR(255) NOT NULL UNIQUE,
  employees_password VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS admins (
  admins_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  admins_name VARCHAR(255) NOT NULL,
  admins_username VARCHAR(50) NOT NULL UNIQUE,
  admins_email VARCHAR(255) NOT NULL UNIQUE,
  admins_password VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
  accounts_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  account_number BIGINT UNSIGNED UNIQUE,
  customers_id INT UNSIGNED NOT NULL,
  employees_id INT UNSIGNED,
  balance DECIMAL(13,2) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (customers_id) REFERENCES customers(customers_id),
  FOREIGN KEY (employees_id) REFERENCES employees(employees_id)
);

CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    customers_id INT UNSIGNED,
    employees_id INT UNSIGNED,
    appointment_date VARCHAR(255),
    FOREIGN KEY (customers_id) REFERENCES customers (customers_id),
    FOREIGN KEY (employees_id) REFERENCES employees (employees_id)
);

CREATE TABLE IF NOT EXISTS messages (
  message_id INT PRIMARY KEY AUTO_INCREMENT,
  customers_id INT UNSIGNED,
  employees_id INT UNSIGNED,
  messageContent VARCHAR(255),
  FOREIGN KEY (customers_id) REFERENCES customers(customers_id),
  FOREIGN KEY (employees_id) REFERENCES employees(employees_id)
);

ALTER TABLE accounts MODIFY COLUMN employees_id INT NULL;

INSERT INTO admins (admins_id, admins_name, admins_username, admins_email, admins_password)
VALUES (0, 'admin', 'admin', 'admin@bank.com', SHA2('admin', 256));