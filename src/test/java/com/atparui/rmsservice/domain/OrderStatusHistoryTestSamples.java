package com.atparui.rmsservice.domain;

import java.util.UUID;

public class OrderStatusHistoryTestSamples {

    public static OrderStatusHistory getOrderStatusHistorySample1() {
        return new OrderStatusHistory()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .previousStatus("previousStatus1")
            .newStatus("newStatus1")
            .changedBy("changedBy1");
    }

    public static OrderStatusHistory getOrderStatusHistorySample2() {
        return new OrderStatusHistory()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .previousStatus("previousStatus2")
            .newStatus("newStatus2")
            .changedBy("changedBy2");
    }

    public static OrderStatusHistory getOrderStatusHistoryRandomSampleGenerator() {
        return new OrderStatusHistory()
            .id(UUID.randomUUID())
            .previousStatus(UUID.randomUUID().toString())
            .newStatus(UUID.randomUUID().toString())
            .changedBy(UUID.randomUUID().toString());
    }
}
