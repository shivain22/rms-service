package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static OrderItem getOrderItemSample1() {
        return new OrderItem().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).quantity(1).status("status1");
    }

    public static OrderItem getOrderItemSample2() {
        return new OrderItem().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).quantity(2).status("status2");
    }

    public static OrderItem getOrderItemRandomSampleGenerator() {
        return new OrderItem().id(UUID.randomUUID()).quantity(intCount.incrementAndGet()).status(UUID.randomUUID().toString());
    }
}
