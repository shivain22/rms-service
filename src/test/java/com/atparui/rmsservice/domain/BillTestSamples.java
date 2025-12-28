package com.atparui.rmsservice.domain;

import java.util.UUID;

public class BillTestSamples {

    public static Bill getBillSample1() {
        return new Bill()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .billNumber("billNumber1")
            .status("status1")
            .generatedBy("generatedBy1");
    }

    public static Bill getBillSample2() {
        return new Bill()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .billNumber("billNumber2")
            .status("status2")
            .generatedBy("generatedBy2");
    }

    public static Bill getBillRandomSampleGenerator() {
        return new Bill()
            .id(UUID.randomUUID())
            .billNumber(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .generatedBy(UUID.randomUUID().toString());
    }
}
