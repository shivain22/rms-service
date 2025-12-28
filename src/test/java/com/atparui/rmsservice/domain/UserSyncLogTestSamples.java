package com.atparui.rmsservice.domain;

import java.util.UUID;

public class UserSyncLogTestSamples {

    public static UserSyncLog getUserSyncLogSample1() {
        return new UserSyncLog()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .syncType("syncType1")
            .syncStatus("syncStatus1")
            .externalUserId("externalUserId1")
            .syncedBy("syncedBy1");
    }

    public static UserSyncLog getUserSyncLogSample2() {
        return new UserSyncLog()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .syncType("syncType2")
            .syncStatus("syncStatus2")
            .externalUserId("externalUserId2")
            .syncedBy("syncedBy2");
    }

    public static UserSyncLog getUserSyncLogRandomSampleGenerator() {
        return new UserSyncLog()
            .id(UUID.randomUUID())
            .syncType(UUID.randomUUID().toString())
            .syncStatus(UUID.randomUUID().toString())
            .externalUserId(UUID.randomUUID().toString())
            .syncedBy(UUID.randomUUID().toString());
    }
}
