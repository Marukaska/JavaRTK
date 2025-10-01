package ru.rtk.java.attestation.attestation_03;

import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class App {
    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream in = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                System.err.println("Не найден application.properties в resources");
                return;
            }
            props.load(in);
        } catch (IOException e) {
            System.err.println("Ошибка чтения application.properties: " + e.getMessage());
            return;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        boolean flywayEnabled = Boolean.parseBoolean(props.getProperty("flyway.enabled", "true"));

        if (flywayEnabled) {
            System.out.println("===> Запуск миграций Flyway...");
            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, password)
                    .locations("filesystem:src/ru/rtk/java/attestation/attestation_03/resources/db/migration")
                    .load();
            flyway.migrate();
        }

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);
            try {
                System.out.println("===> Старт транзакции CRUD");

                long newProductId = insertProduct(conn, "Тестовый товар", 999.99, 5, "Тест");
                long newCustomerId = insertCustomer(conn, "Тест", "Покупатель", "+7-999-000-00-00", "test.buyer@example.com");

                long newStatusId = getStatusId(conn, "NEW");
                long newOrderId = createOrder(conn, newProductId, newCustomerId, 2, newStatusId);

                updateProductPrice(conn, newProductId, 1099.99);
                addProductStock(conn, newProductId, 3);

                printLastOrders(conn, 5);

                deleteOrder(conn, newOrderId);
                deleteCustomer(conn, newCustomerId);
                deleteProduct(conn, newProductId);

                conn.commit();
                System.out.println("===> Транзакция успешно зафиксирована (COMMIT)");
            } catch (Exception e) {
                System.err.println("Ошибка, откат транзакции: " + e.getMessage());
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static long insertProduct(Connection conn, String description, double price, int qty, String category) throws SQLException {
        String sql = "INSERT INTO product(description, price, quantity, category) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setBigDecimal(2, new java.math.BigDecimal(price).setScale(2, java.math.RoundingMode.HALF_UP));
            ps.setInt(3, qty);
            ps.setString(4, category);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                long id = rs.getLong(1);
                System.out.printf("Вставлен товар: id=%d, %s, %.2f, qty=%d, cat=%s%n", id, description, price, qty, category);
                return id;
            }
        }
    }

    private static long insertCustomer(Connection conn, String firstName, String lastName, String phone, String email) throws SQLException {
        String sql = "INSERT INTO customer(first_name, last_name, phone, email) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phone);
            ps.setString(4, email);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                long id = rs.getLong(1);
                System.out.printf("Вставлен покупатель: id=%d, %s %s%n", id, firstName, lastName);
                return id;
            }
        }
    }

    private static long getStatusId(Connection conn, String statusName) throws SQLException {
        String sql = "SELECT id FROM order_status WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Статус не найден: " + statusName);
                return rs.getLong(1);
            }
        }
    }

    private static long createOrder(Connection conn, long productId, long customerId, int qty, long statusId) throws SQLException {
        String insertOrder = "INSERT INTO orders(product_id, customer_id, order_date, quantity, status_id) VALUES (?, ?, now(), ?, ?) RETURNING id";
        String updateStock = "UPDATE product SET quantity = quantity - ? WHERE id = ?";

        try (PreparedStatement psOrder = conn.prepareStatement(insertOrder);
             PreparedStatement psStock = conn.prepareStatement(updateStock)) {

            psOrder.setLong(1, productId);
            psOrder.setLong(2, customerId);
            psOrder.setInt(3, qty);
            psOrder.setLong(4, statusId);

            long orderId;
            try (ResultSet rs = psOrder.executeQuery()) {
                rs.next();
                orderId = rs.getLong(1);
            }

            psStock.setInt(1, qty);
            psStock.setLong(2, productId);
            int updated = psStock.executeUpdate();
            if (updated != 1) throw new SQLException("Не удалось обновить остаток товара id=" + productId);

            System.out.printf("Создан заказ: id=%d, product=%d, customer=%d, qty=%d, status=%d%n",
                    orderId, productId, customerId, qty, statusId);
            return orderId;
        }
    }

    private static void updateProductPrice(Connection conn, long productId, double newPrice) throws SQLException {
        String sql = "UPDATE product SET price = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, new java.math.BigDecimal(newPrice).setScale(2, java.math.RoundingMode.HALF_UP));
            ps.setLong(2, productId);
            int updated = ps.executeUpdate();
            System.out.printf("Обновлена цена товара id=%d, строк изменено: %d%n", productId, updated);
        }
    }

    private static void addProductStock(Connection conn, long productId, int addQty) throws SQLException {
        String sql = "UPDATE product SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addQty);
            ps.setLong(2, productId);
            int updated = ps.executeUpdate();
            System.out.printf("Пополнен склад товара id=%d на %+d, строк изменено: %d%n", productId, addQty, updated);
        }
    }

    private static void deleteOrder(Connection conn, long orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            int deleted = ps.executeUpdate();
            System.out.printf("Удалён заказ id=%d, строк удалено: %d%n", orderId, deleted);
        }
    }

    private static void deleteCustomer(Connection conn, long customerId) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            int deleted = ps.executeUpdate();
            System.out.printf("Удалён покупатель id=%d, строк удалено: %d%n", customerId, deleted);
        }
    }

    private static void deleteProduct(Connection conn, long productId) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            int deleted = ps.executeUpdate();
            System.out.printf("Удалён товар id=%d, строк удалено: %d%n", productId, deleted);
        }
    }

    private static void printLastOrders(Connection conn, int limit) throws SQLException {
        String sql =
            "SELECT o.id, o.order_date, c.first_name, c.last_name, " +
            "       p.description, o.quantity, os.name AS status, p.price, (p.price * o.quantity) AS total " +
            "FROM orders o " +
            "JOIN customer c ON c.id = o.customer_id " +
            "JOIN product  p ON p.id = o.product_id " +
            "JOIN order_status os ON os.id = o.status_id " +
            "ORDER BY o.order_date DESC " +
            "LIMIT ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Последние заказы:");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                while (rs.next()) {
                    long id = rs.getLong("id");
                    Timestamp ts = rs.getTimestamp("order_date");
                    String customer = rs.getString("first_name") + " " + rs.getString("last_name");
                    String product = rs.getString("description");
                    int qty = rs.getInt("quantity");
                    String status = rs.getString("status");
                    double price = rs.getDouble("price");
                    double total = rs.getDouble("total");
                    System.out.printf("#%d | %s | %-18s | %-18s | qty=%d | %s | price=%.2f | total=%.2f%n",
                            id,
                            ts.toLocalDateTime().format(fmt),
                            customer,
                            product,
                            qty,
                            status,
                            price,
                            total);
                }
            }
        }
    }
}
