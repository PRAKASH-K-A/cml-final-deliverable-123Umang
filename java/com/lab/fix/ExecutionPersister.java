package com.lab.fix;

import java.util.concurrent.BlockingQueue;

public class ExecutionPersister implements Runnable {

    private BlockingQueue<Execution> queue;

    public ExecutionPersister(BlockingQueue<Execution> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

        System.out.println("🔥 Execution Persister Started...");

        while (true) {
            try {
                Execution exec = queue.take();

                DatabaseManager.insertExecution(
                        exec.getOrderId(),
                        exec.getSymbol(),
                        exec.getSide(),
                        exec.getExecQty(),
                        exec.getExecPrice()
                );

                System.out.println("✅ Execution Saved: " + exec.getSymbol());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}