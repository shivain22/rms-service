package com.atparui.rmsservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AppNavigationMenuItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static AppNavigationMenuItem getAppNavigationMenuItemSample1() {
        return new AppNavigationMenuItem()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .itemCode("itemCode1")
            .itemName("itemName1")
            .icon("icon1")
            .routePath("routePath1")
            .componentPath("componentPath1")
            .displayOrder(1)
            .badgeText("badgeText1")
            .badgeColor("badgeColor1");
    }

    public static AppNavigationMenuItem getAppNavigationMenuItemSample2() {
        return new AppNavigationMenuItem()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .itemCode("itemCode2")
            .itemName("itemName2")
            .icon("icon2")
            .routePath("routePath2")
            .componentPath("componentPath2")
            .displayOrder(2)
            .badgeText("badgeText2")
            .badgeColor("badgeColor2");
    }

    public static AppNavigationMenuItem getAppNavigationMenuItemRandomSampleGenerator() {
        return new AppNavigationMenuItem()
            .id(UUID.randomUUID())
            .itemCode(UUID.randomUUID().toString())
            .itemName(UUID.randomUUID().toString())
            .icon(UUID.randomUUID().toString())
            .routePath(UUID.randomUUID().toString())
            .componentPath(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet())
            .badgeText(UUID.randomUUID().toString())
            .badgeColor(UUID.randomUUID().toString());
    }
}
