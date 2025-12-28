package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BranchTableTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BranchTable getBranchTableSample1() {
        return new BranchTable()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .tableNumber("tableNumber1")
            .tableName("tableName1")
            .capacity(1)
            .floor("floor1")
            .section("section1")
            .status("status1")
            .qrCode("qrCode1")
            .qrCodeUrl("qrCodeUrl1");
    }

    public static BranchTable getBranchTableSample2() {
        return new BranchTable()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .tableNumber("tableNumber2")
            .tableName("tableName2")
            .capacity(2)
            .floor("floor2")
            .section("section2")
            .status("status2")
            .qrCode("qrCode2")
            .qrCodeUrl("qrCodeUrl2");
    }

    public static BranchTable getBranchTableRandomSampleGenerator() {
        return new BranchTable()
            .id(UUID.randomUUID())
            .tableNumber(UUID.randomUUID().toString())
            .tableName(UUID.randomUUID().toString())
            .capacity(intCount.incrementAndGet())
            .floor(UUID.randomUUID().toString())
            .section(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .qrCode(UUID.randomUUID().toString())
            .qrCodeUrl(UUID.randomUUID().toString());
    }
}
