package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MenuItem getMenuItemSample1() {
        return new MenuItem()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .code("code1")
            .itemType("itemType1")
            .cuisineType("cuisineType1")
            .spiceLevel(1)
            .preparationTime(1)
            .imageUrl("imageUrl1")
            .displayOrder(1);
    }

    public static MenuItem getMenuItemSample2() {
        return new MenuItem()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .code("code2")
            .itemType("itemType2")
            .cuisineType("cuisineType2")
            .spiceLevel(2)
            .preparationTime(2)
            .imageUrl("imageUrl2")
            .displayOrder(2);
    }

    public static MenuItem getMenuItemRandomSampleGenerator() {
        return new MenuItem()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .itemType(UUID.randomUUID().toString())
            .cuisineType(UUID.randomUUID().toString())
            .spiceLevel(intCount.incrementAndGet())
            .preparationTime(intCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
