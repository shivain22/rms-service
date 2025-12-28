package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuItemVariantTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MenuItemVariant getMenuItemVariantSample1() {
        return new MenuItemVariant()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .variantName("variantName1")
            .variantCode("variantCode1")
            .displayOrder(1);
    }

    public static MenuItemVariant getMenuItemVariantSample2() {
        return new MenuItemVariant()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .variantName("variantName2")
            .variantCode("variantCode2")
            .displayOrder(2);
    }

    public static MenuItemVariant getMenuItemVariantRandomSampleGenerator() {
        return new MenuItemVariant()
            .id(UUID.randomUUID())
            .variantName(UUID.randomUUID().toString())
            .variantCode(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
