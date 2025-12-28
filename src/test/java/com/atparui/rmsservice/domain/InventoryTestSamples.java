package com.atparui.rmsservice.domain;

import java.util.UUID;

public class InventoryTestSamples {

    public static Inventory getInventorySample1() {
        return new Inventory().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).unit("unit1").lastUpdatedBy("lastUpdatedBy1");
    }

    public static Inventory getInventorySample2() {
        return new Inventory().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).unit("unit2").lastUpdatedBy("lastUpdatedBy2");
    }

    public static Inventory getInventoryRandomSampleGenerator() {
        return new Inventory().id(UUID.randomUUID()).unit(UUID.randomUUID().toString()).lastUpdatedBy(UUID.randomUUID().toString());
    }
}
