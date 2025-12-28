package com.atparui.rmsservice.domain;

import java.util.UUID;

public class PaymentMethodTestSamples {

    public static PaymentMethod getPaymentMethodSample1() {
        return new PaymentMethod()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .methodCode("methodCode1")
            .methodName("methodName1");
    }

    public static PaymentMethod getPaymentMethodSample2() {
        return new PaymentMethod()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .methodCode("methodCode2")
            .methodName("methodName2");
    }

    public static PaymentMethod getPaymentMethodRandomSampleGenerator() {
        return new PaymentMethod().id(UUID.randomUUID()).methodCode(UUID.randomUUID().toString()).methodName(UUID.randomUUID().toString());
    }
}
