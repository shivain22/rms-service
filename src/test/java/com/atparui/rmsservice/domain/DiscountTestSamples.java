package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscountTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Discount getDiscountSample1() {
        return new Discount()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .discountCode("discountCode1")
            .discountName("discountName1")
            .discountType("discountType1")
            .applicableTo("applicableTo1")
            .maxUses(1)
            .currentUses(1);
    }

    public static Discount getDiscountSample2() {
        return new Discount()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .discountCode("discountCode2")
            .discountName("discountName2")
            .discountType("discountType2")
            .applicableTo("applicableTo2")
            .maxUses(2)
            .currentUses(2);
    }

    public static Discount getDiscountRandomSampleGenerator() {
        return new Discount()
            .id(UUID.randomUUID())
            .discountCode(UUID.randomUUID().toString())
            .discountName(UUID.randomUUID().toString())
            .discountType(UUID.randomUUID().toString())
            .applicableTo(UUID.randomUUID().toString())
            .maxUses(intCount.incrementAndGet())
            .currentUses(intCount.incrementAndGet());
    }
}
