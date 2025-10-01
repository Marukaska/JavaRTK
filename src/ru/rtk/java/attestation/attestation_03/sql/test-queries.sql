SELECT o.id, o.order_date, c.first_name || ' ' || c.last_name AS customer,
       p.description AS product, o.quantity, os.name AS status
FROM orders o
JOIN customer c ON c.id = o.customer_id
JOIN product  p ON p.id = o.product_id
JOIN order_status os ON os.id = o.status_id
WHERE o.order_date >= now() - interval '7 days'
ORDER BY o.order_date DESC;

SELECT p.id, p.description, SUM(o.quantity) AS total_qty
FROM orders o JOIN product p ON p.id = o.product_id
GROUP BY p.id, p.description
ORDER BY total_qty DESC LIMIT 3;

SELECT c.id, c.first_name, c.last_name, SUM(p.price * o.quantity) AS total_spent
FROM orders o JOIN customer c ON c.id = o.customer_id
JOIN product p ON p.id = o.product_id
GROUP BY c.id, c.first_name, c.last_name
ORDER BY total_spent DESC;

SELECT id, description, quantity FROM product WHERE quantity <= 10 ORDER BY quantity ASC;

SELECT os.name AS status, COUNT(*) AS cnt
FROM orders o JOIN order_status os ON os.id = o.status_id
GROUP BY os.name ORDER BY cnt DESC;

UPDATE product SET price = price * 1.10 WHERE id = 1;
UPDATE product SET quantity = quantity + 10 WHERE id = 1;

-- "Старый" NEW-заказ (40 дней назад) для обновления
WITH ids AS (
  SELECT
    (SELECT id FROM product  ORDER BY id LIMIT 1)  AS product_id,
    (SELECT id FROM customer ORDER BY id LIMIT 1)  AS customer_id,
    (SELECT id FROM order_status WHERE name='NEW') AS new_status
)
INSERT INTO orders(product_id, customer_id, order_date, quantity, status_id)
SELECT product_id, customer_id, now() - interval '40 days', 1, new_status FROM ids
RETURNING id;

UPDATE orders
SET status_id = (SELECT id FROM order_status WHERE name = 'CANCELLED')
WHERE status_id = (SELECT id FROM order_status WHERE name = 'NEW')
  AND order_date < now() - interval '30 days';

-- Клиент без заказов для удаления
INSERT INTO customer(first_name, last_name, phone, email)
VALUES ('Тест', 'БезЗаказов', '+7-900-000-00-01', 'noorders1@example.com')
RETURNING id;

-- Товар без заказов для удаления
INSERT INTO product(description, price, quantity, category)
VALUES ('Тестовый товар без заказов', 1.00, 1, 'Тест')
RETURNING id;

DELETE FROM customer c WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = c.id);
DELETE FROM product p WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.product_id = p.id);
