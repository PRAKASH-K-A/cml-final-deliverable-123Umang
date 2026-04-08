package com.lab.fix;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/trading_system";
    private static final String USER = "root";
    private static final String PASS = "Umang21";

    private static final int BATCH_SIZE = 100;

    private static Connection conn;
    private static PreparedStatement orderStmt;
    private static PreparedStatement execStmt;

    private static int orderCount = 0;
    private static int execCount = 0;

    // ===========================
    // INITIALIZATION (RUN ONCE)
    // ===========================
    static {
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            conn.setAutoCommit(false);

            // ORDER INSERT
            String orderSql = "INSERT INTO orders " +
                    "(order_id, cl_ord_id, customer_code, symbol, side, price, quantity, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            orderStmt = conn.prepareStatement(orderSql);

            // EXECUTION INSERT
            String execSql = "INSERT INTO executions " +
                    "(exec_id, order_id, symbol, side, exec_qty, exec_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            execStmt = conn.prepareStatement(execSql);

            System.out.println("✅ DB Initialized");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // INSERT ORDER (BATCH)
    // ===========================
    public static void insertOrder(Order order) {
        try {
            orderStmt.setString(1, order.getOrderId());
            orderStmt.setString(2, order.getClOrdID());
            orderStmt.setString(3, order.getCustomerCode());
            orderStmt.setString(4, order.getSymbol());
            orderStmt.setString(5, String.valueOf(order.getSide()));
            orderStmt.setDouble(6, order.getPrice());
            orderStmt.setInt(7, order.getQuantity());
            orderStmt.setString(8, "NEW");

            orderStmt.addBatch();
            orderCount++;

            if (orderCount % BATCH_SIZE == 0) {
                orderStmt.executeBatch();
                conn.commit();
                System.out.println("✅ Order Batch Inserted: " + orderCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // INSERT EXECUTION (BATCH)
    // ===========================
    public static void insertExecution(String orderId, String symbol,
                                       char side, int qty, double price) {
        try {
            execStmt.setString(1, UUID.randomUUID().toString());
            execStmt.setString(2, orderId);
            execStmt.setString(3, symbol);
            execStmt.setString(4, String.valueOf(side));
            execStmt.setInt(5, qty);
            execStmt.setDouble(6, price);

            execStmt.addBatch();
            execCount++;

            if (execCount % BATCH_SIZE == 0) {
                execStmt.executeBatch();
                conn.commit();
                System.out.println("✅ Execution Batch Inserted: " + execCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // FINAL FLUSH (VERY IMPORTANT)
    // ===========================
    public static void flush() {
        try {
            if (orderStmt != null) orderStmt.executeBatch();
            if (execStmt != null) execStmt.executeBatch();

            conn.commit();

            System.out.println("✅ FINAL FLUSH DONE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // CLOSE CONNECTION
    // ===========================
    public static void close() {
        try {
            flush();

            if (orderStmt != null) orderStmt.close();
            if (execStmt != null) execStmt.close();
            if (conn != null) conn.close();

            System.out.println("✅ DB CLOSED");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}