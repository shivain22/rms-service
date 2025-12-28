package com.atparui.rmsservice.domain;

import java.util.UUID;

public class MenuItemAddonTestSamples {

    public static MenuItemAddon getMenuItemAddonSample1() {
        return new MenuItemAddon()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .addonName("addonName1")
            .addonCode("addonCode1");
    }

    public static MenuItemAddon getMenuItemAddonSample2() {
        return new MenuItemAddon()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .addonName("addonName2")
            .addonCode("addonCode2");
    }

    public static MenuItemAddon getMenuItemAddonRandomSampleGenerator() {
        return new MenuItemAddon().id(UUID.randomUUID()).addonName(UUID.randomUUID().toString()).addonCode(UUID.randomUUID().toString());
    }
}
