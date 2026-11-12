USE restman6;
-- Bảng số liệu mong đợi; dùng cùng SQL với báo cáo.
SELECT f.id, f.name, SUM(d.quantity*d.unitPrice) AS revenue
FROM Food6 f JOIN OrderDetail6 d ON d.foodId=f.id
JOIN Invoice6 i ON i.orderId=d.orderId
WHERE i.status='PAID' AND i.paidAt >= '2026-10-01' AND i.paidAt < '2026-11-01'
GROUP BY f.id,f.name ORDER BY revenue DESC,f.id;
SELECT SUM(quantity*unitPrice) AS invoice201Total FROM OrderDetail6 WHERE orderId=101;
SELECT COUNT(*) AS tableCount FROM information_schema.tables
WHERE table_schema='restman6' AND table_type='BASE TABLE';
