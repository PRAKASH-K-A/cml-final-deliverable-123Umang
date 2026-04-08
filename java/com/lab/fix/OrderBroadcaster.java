package com.lab.fix;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import com.google.gson.Gson;

public class OrderBroadcaster extends WebSocketServer {

    private Gson gson = new Gson();

    public OrderBroadcaster(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("UI Connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("UI Disconnected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from UI: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("WebSocket Error:");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server started on port 8081");
    }

    // ========================================== 
    // 🔥 BROADCAST ORDER (UNCHANGED + SAFE CHECK)
    // ==========================================
    public void broadcastOrder(Order order) {

        try {
            if (order == null) return;

            String json = gson.toJson(order);

            // optional log (safe)
            System.out.println("Broadcasting Order: " + json);

            broadcast(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendTradeUpdate(Execution trade) {
        try {
            String json = gson.toJson(trade);
            broadcast(json);
            System.out.println("📡 Trade Broadcasted: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

