package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BillItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BillItem getBillItemSample1() {
        return new BillItem().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).itemName("itemName1").quantity(1);
    }

    public static BillItem getBillItemSample2() {
        return new BillItem().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).itemName("itemName2").quantity(2);
    }

    public static BillItem getBillItemRandomSampleGenerator() {
        return new BillItem().id(UUID.randomUUID()).itemName(UUID.randomUUID().toString()).quantity(intCount.incrementAndGet());
    }
}
