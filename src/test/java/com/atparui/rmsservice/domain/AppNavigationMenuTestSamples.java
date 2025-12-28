package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AppNavigationMenuTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static AppNavigationMenu getAppNavigationMenuSample1() {
        return new AppNavigationMenu()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .menuCode("menuCode1")
            .menuName("menuName1")
            .menuType("menuType1")
            .icon("icon1")
            .routePath("routePath1")
            .displayOrder(1);
    }

    public static AppNavigationMenu getAppNavigationMenuSample2() {
        return new AppNavigationMenu()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .menuCode("menuCode2")
            .menuName("menuName2")
            .menuType("menuType2")
            .icon("icon2")
            .routePath("routePath2")
            .displayOrder(2);
    }

    public static AppNavigationMenu getAppNavigationMenuRandomSampleGenerator() {
        return new AppNavigationMenu()
            .id(UUID.randomUUID())
            .menuCode(UUID.randomUUID().toString())
            .menuName(UUID.randomUUID().toString())
            .menuType(UUID.randomUUID().toString())
            .icon(UUID.randomUUID().toString())
            .routePath(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
