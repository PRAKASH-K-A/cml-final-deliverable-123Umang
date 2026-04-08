package com.lab.fix;

import java.util.*;
import java.util.concurrent.BlockingQueue;

import quickfix.field.Side;

public class MatchingEngineApp {

    private OrderBook orderBook;
    private BlockingQueue<Execution> execQueue;

    public MatchingEngineApp(OrderBook orderBook,BlockingQueue<Execution> execQueue) {
        this.orderBook = orderBook;
        this.execQueue = execQueue;
    }

    public void process(Order order) {
    
    
        String symbol = order.getSymbol().trim().toUpperCase();

        System.out.println("Processing Order: " + order.getClOrdID());

        if (order.getSide()==Side.BUY || order.getSide() == '1') {
            matchBuy(order);
        } else if (order.getSide()==Side.SELL || order.getSide() == '2') {
            matchSell(order);
        }
	
    }

    // ================= BUY =================
    private void matchBuy(Order buyOrder) {

        while (buyOrder.getQuantity() > 0 &&
               !orderBook.getAsks().isEmpty()) {

            double bestAsk = orderBook.getAsks().firstKey();

            if (Double.compare(buyOrder.getPrice(), bestAsk) < 0)
                break;

            List<Order> sellList =
            	    orderBook.getAsks().get(bestAsk);

            	// 🚨 CRITICAL FIX
            	if (sellList == null || sellList.isEmpty()) {
            	    orderBook.getAsks().remove(bestAsk); // clean empty level
            	    continue; // skip this price level safely
            	}

            	Order sellOrder = sellList.get(0);

            int qty = Math.min(
                    buyOrder.getQuantity(),
                    sellOrder.getQuantity()
            );

            executeTrade(buyOrder, sellOrder, qty, bestAsk);

            buyOrder.setQuantity(buyOrder.getQuantity() - qty);
            sellOrder.setQuantity(sellOrder.getQuantity() - qty);

            if (sellOrder.getQuantity() == 0) {
                sellList.remove(0);

                // 🚨 CRITICAL FIX
                if (sellList.isEmpty()) {
                    orderBook.getAsks().remove(bestAsk);
                }
            }
        if (buyOrder.getQuantity() > 0) {
            orderBook.getBids()
                    .computeIfAbsent(
                            buyOrder.getPrice(),
                            k -> new ArrayList<>()
                    )
                    .add(buyOrder);
        }
    }
        }
    

    // ================= SELL =================
    private void matchSell(Order sellOrder) {

        while (sellOrder.getQuantity() > 0 &&
               !orderBook.getBids().isEmpty()) {

            double bestBid = orderBook.getBids().firstKey();

            if (Double.compare(sellOrder.getPrice(), bestBid) > 0)
                break;

            List<Order> buyList =
                    orderBook.getBids().get(bestBid);

            Order buyOrder = buyList.get(0);

            int qty = Math.min(
                    sellOrder.getQuantity(),
                    buyOrder.getQuantity()
            );

            executeTrade(buyOrder, sellOrder, qty, bestBid);

            sellOrder.setQuantity(sellOrder.getQuantity() - qty);
            buyOrder.setQuantity(buyOrder.getQuantity() - qty);

            if (buyOrder.getQuantity() == 0) {
                buyList.remove(0);

                if (buyList.isEmpty()) {
                    orderBook.getBids().remove(bestBid);
                }
            }
        }

        if (sellOrder.getQuantity() > 0) {
            orderBook.getAsks()
                    .computeIfAbsent(
                            sellOrder.getPrice(),
                            k -> new ArrayList<>()
                    )
                    .add(sellOrder);
        }
    }

    // ================= EXECUTION =================
    private void executeTrade(Order buyOrder,
                              Order sellOrder,
                              int qty,
                              double price) {

        System.out.println("🔥 EXECUTE TRADE");

        Execution buyExec = new Execution(
                buyOrder.getOrderId(),
                buyOrder.getSymbol(),
                '1',
                qty,
                price,
                buyOrder.getClOrdID()
        );

        Execution sellExec = new Execution(
                sellOrder.getOrderId(),
                sellOrder.getSymbol(),
                '2',
                qty,
                price,
                sellOrder.getClOrdID()
              
        );

        execQueue.add(buyExec);
        execQueue.add(sellExec);
    }
}