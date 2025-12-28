package com.atparui.rmsservice.domain;

import java.util.UUID;

public class OrderTestSamples {

    public static Order getOrderSample1() {
        return new Order()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .orderNumber("orderNumber1")
            .orderType("orderType1")
            .orderSource("orderSource1")
            .status("status1")
            .cancelledBy("cancelledBy1");
    }

    public static Order getOrderSample2() {
        return new Order()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .orderNumber("orderNumber2")
            .orderType("orderType2")
            .orderSource("orderSource2")
            .status("status2")
            .cancelledBy("cancelledBy2");
    }

    public static Order getOrderRandomSampleGenerator() {
        return new Order()
            .id(UUID.randomUUID())
            .orderNumber(UUID.randomUUID().toString())
            .orderType(UUID.randomUUID().toString())
            .orderSource(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .cancelledBy(UUID.randomUUID().toString());
    }
}
