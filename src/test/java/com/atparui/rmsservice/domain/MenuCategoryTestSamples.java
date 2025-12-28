package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MenuCategory getMenuCategorySample1() {
        return new MenuCategory()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .code("code1")
            .displayOrder(1)
            .imageUrl("imageUrl1");
    }

    public static MenuCategory getMenuCategorySample2() {
        return new MenuCategory()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .code("code2")
            .displayOrder(2)
            .imageUrl("imageUrl2");
    }

    public static MenuCategory getMenuCategoryRandomSampleGenerator() {
        return new MenuCategory()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString());
    }
}
