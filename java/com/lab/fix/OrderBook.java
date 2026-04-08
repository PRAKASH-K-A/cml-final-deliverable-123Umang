package com.lab.fix;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class OrderBook {

    private ConcurrentSkipListMap<Double, List<Order>> bids =
            new ConcurrentSkipListMap<>(Collections.reverseOrder());

    private ConcurrentSkipListMap<Double, List<Order>> asks =
            new ConcurrentSkipListMap<>();

    public ConcurrentSkipListMap<Double, List<Order>> getBids() { return bids; }
    public ConcurrentSkipListMap<Double, List<Order>> getAsks() { return asks; }
}