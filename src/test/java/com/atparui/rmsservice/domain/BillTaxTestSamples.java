package com.atparui.rmsservice.domain;

import java.util.UUID;

public class BillTaxTestSamples {

    public static BillTax getBillTaxSample1() {
        return new BillTax().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).taxName("taxName1");
    }

    public static BillTax getBillTaxSample2() {
        return new BillTax().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).taxName("taxName2");
    }

    public static BillTax getBillTaxRandomSampleGenerator() {
        return new BillTax().id(UUID.randomUUID()).taxName(UUID.randomUUID().toString());
    }
}
