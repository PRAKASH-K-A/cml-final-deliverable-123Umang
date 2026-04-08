package com.lab.fix;

import java.util.concurrent.BlockingQueue;

public class OrderPersister implements Runnable {

    private BlockingQueue<Order> queue;

    public OrderPersister(BlockingQueue<Order> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

        System.out.println("Order Persister Started...");

        while (true) {
            try {
                Order order = queue.take();

                DatabaseManager.insertOrder(order);

                System.out.println("Order Saved: " + order.getClOrdID());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}