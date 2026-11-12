-- Dữ liệu giả lập để demo; tài khoản/mật khẩu công khai CHỈ dành cho máy cá nhân.
USE restman6;
SET NAMES utf8mb4;
START TRANSACTION;
INSERT INTO User6(id,fullName,phone,email) VALUES
(1,'Khách hàng minh họa','0900000001','customer@example.test'),
(2,'Quản lí minh họa','0900000002','manager@example.test'),
(3,'Nhân viên bán hàng','0900000003','sales@example.test'),
(4,'Nhân viên kho','0900000004','warehouse@example.test'),
(5,'Tài khoản khóa','0900000005','locked@example.test');
INSERT INTO Account6(id,userId,username,passwordHash,role,status) VALUES
(1,1,'customer6','pbkdf2$600000$M425EhnIiFN7+ZqPfiWK5g==$eEq+O9a0nhXgpWBG2TMImK+e+BGO/VhhdITrrGNr/FE=','CUSTOMER','ACTIVE'),
(2,2,'manager6','pbkdf2$600000$lcVbRwdQBC+YEF0FWeqRmw==$UgD1RoJT2JPCCOM/rMdTOh2Np9tBUxd54WcOTBI/heA=','MANAGER','ACTIVE'),
(3,3,'sales6','pbkdf2$600000$tSwmRwqs/GFHpoZQgCqdkQ==$/GNla+yYKL3BnH05kxAMm3CfI5Ym9T9tpCqGmFbfCFQ=','SALES_STAFF','ACTIVE'),
(4,4,'warehouse6','pbkdf2$600000$Un5E9cl3Y77iHgnpDkILPg==$fFMxMTmnNPQGKEXoeDCrOPE1Sbw1MMsGqdcYzdJ9awk=','WAREHOUSE_STAFF','ACTIVE'),
(5,5,'locked6','pbkdf2$600000$AyhoWyUKsSE2kvJOeBiFEQ==$D+TBbz1kgLJ8NqqUMV4ggX74ClnZGcu2b9gqlA38Fd8=','CUSTOMER','LOCKED');
INSERT INTO Employee6(id,position) VALUES (2,'MANAGER'),(3,'SALES_STAFF'),(4,'WAREHOUSE_STAFF');
INSERT INTO Customer6(id,address) VALUES (1,'Hà Nội'),(5,'Hà Nội');
INSERT INTO Food6(id,name,price,description,status) VALUES
(1,'Cơm gà',65000,'Cơm gà phục vụ tại nhà hàng.', 'AVAILABLE'),
(2,'Phở bò',85000,'Phở bò với nước dùng nóng.', 'AVAILABLE'),
(3,'Nước cam',30000,'Nước cam tươi.', 'AVAILABLE'),
(4,'Bún chả',55000,'Món tạm ngừng phục vụ, không có giao dịch trong tháng.', 'UNAVAILABLE');
INSERT INTO Combo6(id,name,price,description,status) VALUES (1,'Combo cơm gà',90000,'Cơm gà và nước cam. Chưa phân bổ doanh thu combo.', 'AVAILABLE');
INSERT INTO ComboDetail6(id,comboId,foodId,quantity) VALUES (1,1,1,1),(2,1,3,1);
INSERT INTO DiningTable6(id,tableNumber,capacity,status) VALUES (1,'B01',4,'AVAILABLE'),(2,'B02',6,'AVAILABLE');
INSERT INTO Reservation6(id,customerId,tableId,confirmedById,reservationTime,guestCount,status) VALUES
(1,1,1,3,'2026-10-10 11:00:00',2,'CONFIRMED'),(2,1,NULL,NULL,'2026-11-20 18:00:00',3,'PENDING');
INSERT INTO MembershipCard6(id,customerId,issuedById,issueDate,status) VALUES (1,1,3,'2026-09-01','ACTIVE');
INSERT INTO Supplier6(id,name,address,phone,email) VALUES (1,'Nhà cung cấp minh họa','Hà Nội','0900000006','supplier@example.test');
INSERT INTO Ingredient6(id,name,unit,stockQuantity) VALUES (1,'Gạo','kg',50),(2,'Thịt gà','kg',20),(3,'Cam','kg',15);
INSERT INTO ImportReceipt6(id,supplierId,createdById,importDate) VALUES (1,1,4,'2026-09-30 08:00:00');
INSERT INTO ImportReceiptDetail6(id,receiptId,ingredientId,quantity,unitPrice) VALUES
(1,1,1,50,20000),(2,1,2,20,70000),(3,1,3,15,25000);

-- 101 và 102 tạo tổng tháng 10 đúng như báo cáo.
INSERT INTO Order6(id,customerId,tableId,handledById,createdAt,orderType,status) VALUES
(101,1,1,3,'2026-10-10 11:00:00','AT_TABLE','COMPLETED'),
(102,1,2,3,'2026-10-12 11:00:00','AT_TABLE','COMPLETED'),
(103,1,1,3,'2026-10-15 11:00:00','AT_TABLE','OPEN'),
(104,1,1,3,'2026-09-30 18:00:00','AT_TABLE','COMPLETED'),
(105,1,2,3,'2026-10-31 23:30:00','AT_TABLE','COMPLETED'),
(106,1,NULL,NULL,'2026-10-20 10:00:00','ONLINE','CANCELLED'),
(107,1,NULL,NULL,'2026-11-20 10:00:00','ONLINE','PENDING');
INSERT INTO OrderDetail6(id,orderId,foodId,calledAt,quantity,unitPrice) VALUES
(1001,101,1,'2026-10-10 11:05:00',2,60000),
(1002,101,3,'2026-10-10 11:10:00',1,30000),
(1003,101,1,'2026-10-10 11:20:00',1,60000),
(1004,102,2,'2026-10-12 11:02:00',2,85000),
(1005,102,1,'2026-10-12 11:05:00',1,65000),
(1006,102,3,'2026-10-12 11:10:00',2,30000),
(1007,103,1,'2026-10-15 11:05:00',10,65000),
(1008,104,1,'2026-09-30 18:05:00',1,65000),
(1009,105,1,'2026-10-31 23:35:00',1,65000),
(1010,106,2,'2026-10-20 10:05:00',1,85000),
(1011,107,3,'2026-11-20 10:05:00',1,30000);
INSERT INTO Invoice6(id,orderId,paidById,createdAt,paidAt,paymentMethod,status) VALUES
(201,101,3,'2026-10-10 12:00:00','2026-10-10 12:10:00','CASH','PAID'),
(202,102,3,'2026-10-12 12:00:00','2026-10-12 12:10:00','BANK_TRANSFER','PAID'),
(203,103,NULL,'2026-10-15 12:00:00',NULL,NULL,'UNPAID'),
(204,104,3,'2026-09-30 23:58:00','2026-09-30 23:59:59','CASH','PAID'),
(205,105,3,'2026-10-31 23:59:00','2026-11-01 00:00:00','CASH','PAID'),
(206,106,NULL,'2026-10-20 10:10:00',NULL,NULL,'CANCELLED');
COMMIT;
-- Tháng 10: Cơm gà 245000; Phở bò 170000; Nước cam 90000; tổng 505000.
-- Hóa đơn 201: 120000 + 30000 + 60000 = 210000. Đơn 107 chưa có hóa đơn.
