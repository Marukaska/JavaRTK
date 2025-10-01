INSERT INTO order_status (name) VALUES
  ('NEW'), ('PAID'), ('SHIPPED'), ('CANCELLED'), ('RETURNED')
ON CONFLICT DO NOTHING;

-- 10 товаров
INSERT INTO product (description, price, quantity, category) VALUES
 ('Ноутбук 14"',  65000.00, 20, 'Электроника'),
 ('Смартфон 6.5"', 42000.00, 35, 'Электроника'),
 ('Наушники BT',    5900.00, 50, 'Аудио'),
 ('Кофемашина',    27900.00, 10, 'Бытовая техника'),
 ('Рюкзак городской', 3900.00, 40, 'Аксессуары'),
 ('Мышь беспроводная', 1500.00, 60, 'Электроника'),
 ('Клавиатура механическая', 5400.00, 25, 'Электроника'),
 ('Монитор 27"',  23000.00, 15, 'Электроника'),
 ('SSD 1TB',       8200.00, 30, 'Комплектующие'),
 ('Фитнес-браслет', 3100.00, 45, 'Гаджеты')
ON CONFLICT DO NOTHING;

-- 10 покупателей
INSERT INTO customer (first_name, last_name, phone, email) VALUES
 ('Иван',  'Иванов',  '+7-900-111-11-11', 'ivanov@example.com'),
 ('Пётр',  'Петров',  '+7-900-222-22-22', 'petrov@example.com'),
 ('Сергей','Сергеев', '+7-900-333-33-33', 'sergeev@example.com'),
 ('Анна',  'Смирнова','+7-900-444-44-44', 'smirnova@example.com'),
 ('Елена', 'Кузнецова','+7-900-555-55-55','kuznecova@example.com'),
 ('Мария', 'Васильева','+7-900-666-66-66','vasilieva@example.com'),
 ('Олег',  'Морозов', '+7-900-777-77-77', 'morozov@example.com'),
 ('Дмитрий','Фёдоров','+7-900-888-88-88','fedorov@example.com'),
 ('Ольга', 'Соколова','+7-900-999-99-99','sokolova@example.com'),
 ('Наталья','Попова', '+7-901-000-00-00','popova@example.com')
ON CONFLICT DO NOTHING;

-- 12 заказов
INSERT INTO orders (product_id, customer_id, order_date, quantity, status_id) VALUES
 (1, 1, now() - interval '1 day', 1, 2),
 (2, 2, now() - interval '2 days', 2, 3),
 (3, 3, now() - interval '3 days', 1, 2),
 (4, 4, now() - interval '4 days', 1, 1),
 (5, 5, now() - interval '5 days', 3, 2),
 (6, 6, now() - interval '6 days', 2, 3),
 (7, 7, now() - interval '7 days', 1, 4),
 (8, 8, now() - interval '8 days', 1, 5),
 (9, 9, now() - interval '9 days', 4, 2),
 (10,10, now() - interval '10 days',1, 1),
 (2, 1, now() - interval '11 days',1, 3),
 (3, 2, now() - interval '12 days',2, 2);

-- Корректировка остатков по тестовым заказам
UPDATE product p
SET quantity = GREATEST(0, p.quantity - sub.q)
FROM (
  SELECT o.product_id, COALESCE(SUM(o.quantity),0) q
  FROM orders o
  GROUP BY o.product_id
) sub
WHERE p.id = sub.product_id;
