package com.atparui.rmsservice.domain;

import java.util.UUID;

public class PaymentTestSamples {

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .paymentNumber("paymentNumber1")
            .transactionId("transactionId1")
            .status("status1")
            .processedBy("processedBy1")
            .refundedBy("refundedBy1");
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .paymentNumber("paymentNumber2")
            .transactionId("transactionId2")
            .status("status2")
            .processedBy("processedBy2")
            .refundedBy("refundedBy2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(UUID.randomUUID())
            .paymentNumber(UUID.randomUUID().toString())
            .transactionId(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .processedBy(UUID.randomUUID().toString())
            .refundedBy(UUID.randomUUID().toString());
    }
}
