package com.lab.fix;

import quickfix.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AppLauncher {

    public static void main(String[] args) {

        try {

            // ================================
            // 🧠 QUEUE FOR BATCH PROCESSING
            // ================================
            BlockingQueue<Execution> executionQueue = new LinkedBlockingQueue<>();


            // ================================
            // 🔁 START EXECUTION PERSISTER THREAD
            // ================================
            ExecutionPersister execPersister = new ExecutionPersister(executionQueue);
            new Thread(execPersister).start();


            // ================================
            // 📘 MATCHING ENGINE SETUP
            // ================================
            OrderBook book = new OrderBook();

            MatchingEngineApp engine =
                    new MatchingEngineApp(book, executionQueue);


            // ================================
            // ⚡ AUTO GENERATE ORDERS (OPTIONAL)
            // ================================
            new Thread(() -> {
                try {
                    Thread.sleep(3000);

                    for (int i = 1; i <= 100; i++) {

                        Order order = new Order(
                                "ORD-" + i,
                                "CL-" + i,
                                "CLIENT_A",     // must exist in DB
                                "GOOG",         // must exist in DB
                                (i % 2 == 0) ? 'B' : 'S',
                                100 + (i % 5),
                                10
                        );

                        engine.process(order);

                        System.out.println("✔ SENT: " + order.getOrderId());

                        Thread.sleep(50);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


            // ================================
            // 🌐 WEBSOCKET SERVER
            // ================================
            OrderBroadcaster broadcaster = new OrderBroadcaster(8081);
            broadcaster.start();

            System.out.println("WebSocket Server Started on port 8081");


            // ================================
            // 📡 FIX ENGINE SETUP
            // ================================
            SessionSettings settings = new SessionSettings("order-service.cfg");

            OrderApplication application =
                    new OrderApplication(broadcaster, engine, executionQueue);

            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new ScreenLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            SocketAcceptor acceptor =
                    new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);

            acceptor.start();


            System.out.println("===================================");
            System.out.println("🚀 Order Service Started");
            System.out.println("📡 Listening on port 9876...");
            System.out.println("===================================");


            // KEEP SERVER RUNNING
            System.in.read();


            // STOP
            acceptor.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}