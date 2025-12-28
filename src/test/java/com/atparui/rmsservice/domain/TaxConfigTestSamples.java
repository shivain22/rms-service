package com.atparui.rmsservice.domain;

import java.util.UUID;

public class TaxConfigTestSamples {

    public static TaxConfig getTaxConfigSample1() {
        return new TaxConfig()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .taxName("taxName1")
            .taxCode("taxCode1")
            .taxType("taxType1");
    }

    public static TaxConfig getTaxConfigSample2() {
        return new TaxConfig()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .taxName("taxName2")
            .taxCode("taxCode2")
            .taxType("taxType2");
    }

    public static TaxConfig getTaxConfigRandomSampleGenerator() {
        return new TaxConfig()
            .id(UUID.randomUUID())
            .taxName(UUID.randomUUID().toString())
            .taxCode(UUID.randomUUID().toString())
            .taxType(UUID.randomUUID().toString());
    }
}
