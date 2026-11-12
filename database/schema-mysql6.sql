-- Chạy trên một CSDL restman6 RỖNG. Không tự xóa dữ liệu đang có.
CREATE DATABASE IF NOT EXISTS restman6 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci;
ALTER DATABASE restman6 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci;
USE restman6;
SET NAMES utf8mb4;

CREATE TABLE User6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fullName VARCHAR(120) NOT NULL,
    phone VARCHAR(20), email VARCHAR(254)
) ENGINE=InnoDB;
CREATE TABLE Account6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL UNIQUE,
    username VARCHAR(80) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (userId) REFERENCES User6(id),
    CHECK (role IN ('CUSTOMER','MANAGER','SALES_STAFF','WAREHOUSE_STAFF')),
    CHECK (status IN ('ACTIVE','LOCKED'))
) ENGINE=InnoDB;
CREATE TABLE Employee6 (
    id BIGINT PRIMARY KEY,
    position VARCHAR(80) NOT NULL,
    FOREIGN KEY (id) REFERENCES User6(id)
) ENGINE=InnoDB;
CREATE TABLE Customer6 (
    id BIGINT PRIMARY KEY, address VARCHAR(255),
    FOREIGN KEY (id) REFERENCES User6(id)
) ENGINE=InnoDB;
CREATE TABLE Food6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    description TEXT, status VARCHAR(20) NOT NULL,
    CHECK (price >= 0), CHECK (status IN ('AVAILABLE','UNAVAILABLE'))
) ENGINE=InnoDB;
CREATE TABLE Combo6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    description TEXT, status VARCHAR(20) NOT NULL,
    CHECK (price >= 0)
) ENGINE=InnoDB;
CREATE TABLE ComboDetail6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comboId BIGINT NOT NULL, foodId BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (comboId) REFERENCES Combo6(id),
    FOREIGN KEY (foodId) REFERENCES Food6(id),
    UNIQUE (comboId, foodId), CHECK (quantity > 0)
) ENGINE=InnoDB;
CREATE TABLE DiningTable6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tableNumber VARCHAR(20) NOT NULL UNIQUE,
    capacity INT NOT NULL, status VARCHAR(20) NOT NULL,
    CHECK (capacity > 0)
) ENGINE=InnoDB;
CREATE TABLE Reservation6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customerId BIGINT NOT NULL, tableId BIGINT, confirmedById BIGINT,
    reservationTime DATETIME NOT NULL, guestCount INT NOT NULL, status VARCHAR(20) NOT NULL,
    FOREIGN KEY (customerId) REFERENCES Customer6(id),
    FOREIGN KEY (tableId) REFERENCES DiningTable6(id),
    FOREIGN KEY (confirmedById) REFERENCES Employee6(id),
    CHECK (guestCount > 0),
    CHECK (status <> 'CONFIRMED' OR (tableId IS NOT NULL AND confirmedById IS NOT NULL))
) ENGINE=InnoDB;
CREATE TABLE Order6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customerId BIGINT NOT NULL, tableId BIGINT, handledById BIGINT,
    createdAt DATETIME NOT NULL, orderType VARCHAR(20) NOT NULL, status VARCHAR(20) NOT NULL,
    FOREIGN KEY (customerId) REFERENCES Customer6(id),
    FOREIGN KEY (tableId) REFERENCES DiningTable6(id),
    FOREIGN KEY (handledById) REFERENCES Employee6(id),
    CHECK (orderType IN ('AT_TABLE','ONLINE')),
    CHECK (orderType <> 'AT_TABLE' OR tableId IS NOT NULL)
) ENGINE=InnoDB;
CREATE TABLE OrderDetail6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orderId BIGINT NOT NULL, foodId BIGINT NOT NULL,
    calledAt DATETIME NOT NULL, quantity INT NOT NULL, unitPrice DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (orderId) REFERENCES Order6(id),
    FOREIGN KEY (foodId) REFERENCES Food6(id),
    CHECK (quantity > 0), CHECK (unitPrice >= 0)
) ENGINE=InnoDB;
CREATE TABLE Invoice6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orderId BIGINT NOT NULL UNIQUE, paidById BIGINT,
    createdAt DATETIME NOT NULL, paidAt DATETIME,
    paymentMethod VARCHAR(30), status VARCHAR(20) NOT NULL,
    FOREIGN KEY (orderId) REFERENCES Order6(id),
    FOREIGN KEY (paidById) REFERENCES Employee6(id),
    CHECK (status IN ('PAID','UNPAID','CANCELLED')),
    CHECK (status <> 'PAID' OR
        (paidAt IS NOT NULL AND paidById IS NOT NULL AND paymentMethod IS NOT NULL))
) ENGINE=InnoDB;
CREATE TABLE MembershipCard6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customerId BIGINT NOT NULL UNIQUE, issuedById BIGINT NOT NULL,
    issueDate DATE NOT NULL, status VARCHAR(20) NOT NULL,
    FOREIGN KEY (customerId) REFERENCES Customer6(id),
    FOREIGN KEY (issuedById) REFERENCES Employee6(id)
) ENGINE=InnoDB;
CREATE TABLE Ingredient6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL, unit VARCHAR(30) NOT NULL,
    stockQuantity DECIMAL(12,3) NOT NULL, CHECK (stockQuantity >= 0)
) ENGINE=InnoDB;
CREATE TABLE Supplier6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL, address VARCHAR(255), phone VARCHAR(20), email VARCHAR(254)
) ENGINE=InnoDB;
CREATE TABLE ImportReceipt6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplierId BIGINT NOT NULL, createdById BIGINT NOT NULL, importDate DATETIME NOT NULL,
    FOREIGN KEY (supplierId) REFERENCES Supplier6(id),
    FOREIGN KEY (createdById) REFERENCES Employee6(id)
) ENGINE=InnoDB;
CREATE TABLE ImportReceiptDetail6 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiptId BIGINT NOT NULL, ingredientId BIGINT NOT NULL,
    quantity DECIMAL(12,3) NOT NULL, unitPrice DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (receiptId) REFERENCES ImportReceipt6(id),
    FOREIGN KEY (ingredientId) REFERENCES Ingredient6(id),
    CHECK (quantity > 0), CHECK (unitPrice >= 0)
) ENGINE=InnoDB;
CREATE INDEX idx_invoice_paid6 ON Invoice6(status, paidAt);
CREATE INDEX idx_detail_food6 ON OrderDetail6(foodId, orderId);
-- Không tạo bảng thống kê, không lưu tổng tiền dẫn xuất, không dùng ON DELETE CASCADE.
