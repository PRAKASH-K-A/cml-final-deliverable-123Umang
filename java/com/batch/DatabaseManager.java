package com.batch;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {

    // ================= CUSTOMER =================
    public static void insertCustomer(String code, String name, String type, double limit) {

        String sql = "INSERT INTO customer_master VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setDouble(4, limit);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SECURITY =================
    public static void insertSecurity(String symbol, String type) {

        String sql = "INSERT INTO security_master (symbol, security_type) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) { 

            ps.setString(1, symbol);
            ps.setString(2, type);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ORDER =================
    public static void insertOrder(String orderId, String clOrdId,
                                   String customer, String symbol,
                                   char side, double price, int qty) {

        String sql = "INSERT INTO orders " +
                "(order_id, cl_ord_id, symbol, side, price, quantity, status, customer_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, orderId);
            ps.setString(2, clOrdId);
            ps.setString(3, symbol);
            ps.setString(4, String.valueOf(side));
            ps.setDouble(5, price);
            ps.setInt(6, qty);
            ps.setString(7, "NEW");
            ps.setString(8, customer);

            ps.executeUpdate();

            System.out.println("✅ Order Inserted: " + orderId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= EXECUTION =================
    public static void insertExecution(String orderId, String symbol,
                                       char side, int qty, double price) {

        String sql = "INSERT INTO executions " +
                "(exec_id, order_id, symbol, side, exec_qty, exec_price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, orderId);
            ps.setString(3, symbol);
            ps.setString(4, String.valueOf(side));
            ps.setInt(5, qty);
            ps.setDouble(6, price);

            ps.executeUpdate();

            System.out.println("🔥 Execution Created for Order: " + orderId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
