package com.atparui.rmsservice.domain;

import java.util.UUID;

public class AppNavigationMenuRoleTestSamples {

    public static AppNavigationMenuRole getAppNavigationMenuRoleSample1() {
        return new AppNavigationMenuRole().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).role("role1");
    }

    public static AppNavigationMenuRole getAppNavigationMenuRoleSample2() {
        return new AppNavigationMenuRole().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).role("role2");
    }

    public static AppNavigationMenuRole getAppNavigationMenuRoleRandomSampleGenerator() {
        return new AppNavigationMenuRole().id(UUID.randomUUID()).role(UUID.randomUUID().toString());
    }
}
