package com.lab.fix;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;

import java.util.Map;
import java.util.HashMap;
import java.sql.*;

public class OrderApplication implements Application {

    private ConcurrentHashMap<String, Order> orderStore =
            new ConcurrentHashMap<>();

    private OrderBroadcaster broadcaster;
    private MatchingEngineApp matchingEngine;
    private SessionID sessionId;
    private BlockingQueue<Execution> executionQueue;

    private Map<String, Security> validSecurities = new HashMap<>();

	

    public OrderApplication(OrderBroadcaster broadcaster,
                            MatchingEngineApp matchingEngine,BlockingQueue<Execution> executionQueue) {
        this.broadcaster = broadcaster;
        this.matchingEngine = matchingEngine;
        this.executionQueue=executionQueue;
    
        System.out.println("Loading securities at startup");
        loadSecurities();
    }

    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("Session Created: " + sessionId);
        loadSecurities();
    }

    @Override
    public void onLogon(SessionID sessionId) {
    	
        System.out.println("LOGON Success: " + sessionId);
       this.sessionId=sessionId;
    }

    @Override
    public void onLogout(SessionID sessionId) {}

    @Override
    public void toAdmin(Message message, SessionID sessionId) {}

    @Override
    public void fromAdmin(Message message, SessionID sessionId) {}

    @Override
    public void toApp(Message message, SessionID sessionId) {}

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound {
    	  long ingressTime = System.nanoTime();

        String msgType =
                message.getHeader().getString(MsgType.FIELD);

        if (MsgType.ORDER_SINGLE.equals(msgType)) {
          
			processNewOrder(message, sessionId,ingressTime);
        }
    }

    // =========================
    // PROCESS ORDER
    // =========================
    private void processNewOrder(Message message,
                                 SessionID sessionId,Long ingressTime) {

    
    	try {
            // 🔍 DEBUG - print full FIX message
            System.out.println("FULL MESSAGE = " + message);

            // -------------------------------
            // EXTRACT FIELDS FROM FIX
            // -------------------------------
            String clOrdID = message.getString(ClOrdID.FIELD);
            String symbol  = message.getString(Symbol.FIELD);
            char side      = message.getChar(Side.FIELD);

            // ✅ FIXED QTY EXTRACTION (NO ERROR)
            int qty = 0;
            try {
                qty = message.getInt(OrderQty.FIELD);
            } catch (FieldNotFound e) {
                System.out.println("QTY FIELD NOT FOUND ❌");
            }

            // ✅ PRICE
            double price = message.getDouble(Price.FIELD);

            // 🔍 DEBUG
            System.out.println("QTY FROM FIX = " + qty);
            System.out.println("PRICE FROM FIX = " + price);

            String customerCode = "CLIENT_A";


    	        // ----------------------------
    	        // VALIDATIONS
    	        // ----------------------------

    	        // Duplicate Check
    	        if (orderStore.containsKey(clOrdID)) {
    	            sendReject(message, sessionId, "Duplicate ClOrdID");
    	            return;
    	        }

    	        // Symbol Validation
    	        if (!validSecurities.containsKey(symbol)) {
    	            sendReject(message, sessionId, "Invalid Symbol");
    	            return;
    	        }

    	        // ----------------------------
    	        // CREATE ORDER
    	        // ----------------------------
    	        String orderId = "ORD-" + System.nanoTime();


            Order order = new Order(
                    orderId,
                    clOrdID,
                    customerCode,
                    symbol,
                    side,
                    price,
                    qty
            );

            orderStore.put(clOrdID, order);

            System.out.println("📥 ORDER RECEIVED: " + orderId);

            // =========================
            // SAVE TO DB
            // =========================
            DatabaseManager.insertOrder(order);

            // =========================
            // MATCHING ENGINE
            // =========================
            System.out.println("🔥 Sending to Matching Engine: " + orderId);

            matchingEngine.process(order);

            // =========================
            // BROADCAST
            // =========================
            broadcaster.broadcastOrder(order);

            // =========================
            // ACK
            // =========================
            sendAck(message, sessionId);
            long egressTime = System.nanoTime();
            long latency = egressTime - ingressTime;
            PerformanceMonitor.recordLatency(latency);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    


    

  
    // LOAD SECURITY MASTER
    // =========================
    private void loadSecurities() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/trading_system",
                "root", "Umang21");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM security_master")) {

            while (rs.next()) {
                String symbol = rs.getString("symbol");

                validSecurities.put(symbol,
                        new Security(symbol, "CS", "", "", 1));
            }

            System.out.println("✅ Securities Loaded: " + validSecurities.keySet());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SEND ACK
    // =========================
    private void sendAck(Message req, SessionID sessionId) {
        try {
            ExecutionReport ack = new ExecutionReport();

            ack.set(new OrderID("ORD_" + System.nanoTime()));
            ack.set(new ExecID("EXEC_" + System.nanoTime()));
            ack.set(new ClOrdID(req.getString(ClOrdID.FIELD)));
            ack.set(new Symbol(req.getString(Symbol.FIELD)));
            ack.set(new Side(req.getChar(Side.FIELD)));

            ack.set(new ExecType(ExecType.NEW));
            ack.set(new OrdStatus(OrdStatus.NEW));

            Session.sendToTarget(ack, sessionId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SEND REJECT
    // =========================
    private void sendReject(Message req,
                            SessionID sessionId,
                            String reason) {
        try {
            ExecutionReport rej = new ExecutionReport();

            rej.set(new OrderID("REJ_" + System.nanoTime()));
            rej.set(new ExecID("REJ_" + System.nanoTime()));
            rej.set(new ClOrdID(req.getString(ClOrdID.FIELD)));
            rej.set(new Symbol(req.getString(Symbol.FIELD)));

            rej.set(new ExecType(ExecType.REJECTED));
            rej.set(new OrdStatus(OrdStatus.REJECTED));
            rej.set(new Text(reason));

            Session.sendToTarget(rej, sessionId);

        } 
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }
        private void startExecutionProcessor() {

            new Thread(() -> {

                while (true) {
                    try {

                        Execution trade = executionQueue.take();

                        System.out.println("🔥 TRADE RECEIVED: " + trade.getOrderId());

                        // 1. SAVE TO DB
                        DatabaseManager.insertExecution(
                                trade.getOrderId(),
                                trade.getSymbol(),
                                trade.getSide(),
                                trade.getExecQty(),
                                trade.getExecPrice()
                        );

                        // 2. UI UPDATE
                        broadcaster.sendTradeUpdate(trade);

                        // 3. 🔥 MOST IMPORTANT (BLUE)
                        sendFillReport(trade);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }
    
    private void sendFillReport(Execution trade) {
        try {

        	quickfix.fix44.ExecutionReport fixTrade =
        		    new quickfix.fix44.ExecutionReport();

        		fixTrade.set(new OrderID(trade.getOrderId()));
        		fixTrade.set(new ClOrdID(trade.getClOrdID()));
        		fixTrade.set(new ExecID("EXEC_" + System.nanoTime()));
        		 
        		fixTrade.set(new ExecType(ExecType.TRADE));
        		fixTrade.set(new OrdStatus(OrdStatus.FILLED));

        		fixTrade.set(new Symbol(trade.getSymbol()));
        		fixTrade.set(new Side(trade.getSide()));

        		fixTrade.set(new OrderQty(trade.getExecQty()));
        		fixTrade.set(new LastPx(trade.getExecPrice()));
        		fixTrade.set(new LastQty(trade.getExecQty()));
        		fixTrade.set(new CumQty(trade.getExecQty()));
        		fixTrade.set(new LeavesQty(0));
        		fixTrade.set(new AvgPx(trade.getExecPrice()));
        		
           
			Session.sendToTarget(fixTrade, sessionId);

            System.out.println("✅ FILL SENT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}