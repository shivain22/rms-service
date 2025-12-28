package com.atparui.rmsservice.domain;

import java.util.UUID;

public class BillDiscountTestSamples {

    public static BillDiscount getBillDiscountSample1() {
        return new BillDiscount()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .discountCode("discountCode1")
            .discountType("discountType1");
    }

    public static BillDiscount getBillDiscountSample2() {
        return new BillDiscount()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .discountCode("discountCode2")
            .discountType("discountType2");
    }

    public static BillDiscount getBillDiscountRandomSampleGenerator() {
        return new BillDiscount()
            .id(UUID.randomUUID())
            .discountCode(UUID.randomUUID().toString())
            .discountType(UUID.randomUUID().toString());
    }
}
