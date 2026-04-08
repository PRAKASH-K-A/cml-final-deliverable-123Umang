package com.batch;

import java.util.Random;

public class BatchProcessor {

    static String[] customers = {"CLIENT_A", "CLIENT_B"};
    static String[] symbols = {"GOOG", "MSFT", "IBM"};

    public static void main(String[] args) {

        System.out.println("🚀 Batch Started...");

        // STEP 1: Insert reference data (keep as is)
        DatabaseManager.insertCustomer("CLIENT_A", "Alice", "RETAIL", 100000);
        DatabaseManager.insertCustomer("CLIENT_B", "Bob", "INSTITUTIONAL", 500000);

        DatabaseManager.insertSecurity("GOOG", "CS");
        DatabaseManager.insertSecurity("MSFT", "CS");
        DatabaseManager.insertSecurity("IBM", "CS");

        Random rand = new Random();

        // 🔥 NEW: OUTER LOOP (5 cycles)
        for (int batch = 1; batch <= 5; batch++) {

            System.out.println("👉 Starting Batch: " + batch);

            // 🔥 EXISTING LOOP (100 orders) — KEEP SAME LOGIC
            for (int i = 1; i <= 100; i++) {

                String orderId = "ORD-" + System.currentTimeMillis() + i;
                String clOrdId = "CL" + i;

                String customer = customers[rand.nextInt(customers.length)];
                String symbol = symbols[rand.nextInt(symbols.length)];

                char side = rand.nextBoolean() ? 'B' : 'S';
                double price = 90 + rand.nextInt(20);
                int qty = 1 + rand.nextInt(10);

                // Insert Order
                DatabaseManager.insertOrder(
                        orderId, clOrdId, customer, symbol, side, price, qty
                );

                // Insert Execution
                DatabaseManager.insertExecution(
                        orderId, symbol, side, qty, price
                );
            }

            System.out.println("✅ Completed Batch: " + batch);

            // 🔥 WAIT 1 MIN (except last batch)
            if (batch < 5) {
                try {
                    System.out.println("⏳ Waiting 1 minute...");
                    Thread.sleep(60000); // 60000 ms = 1 min
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("🎯 All Batches Completed!");
    }
}