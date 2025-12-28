package com.atparui.rmsservice.domain;

import java.util.UUID;

public class RmsUserTestSamples {

    public static RmsUser getRmsUserSample1() {
        return new RmsUser()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .externalUserId("externalUserId1")
            .username("username1")
            .email("email1")
            .phone("phone1")
            .firstName("firstName1")
            .lastName("lastName1")
            .displayName("displayName1")
            .profileImageUrl("profileImageUrl1")
            .syncStatus("syncStatus1");
    }

    public static RmsUser getRmsUserSample2() {
        return new RmsUser()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .externalUserId("externalUserId2")
            .username("username2")
            .email("email2")
            .phone("phone2")
            .firstName("firstName2")
            .lastName("lastName2")
            .displayName("displayName2")
            .profileImageUrl("profileImageUrl2")
            .syncStatus("syncStatus2");
    }

    public static RmsUser getRmsUserRandomSampleGenerator() {
        return new RmsUser()
            .id(UUID.randomUUID())
            .externalUserId(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .displayName(UUID.randomUUID().toString())
            .profileImageUrl(UUID.randomUUID().toString())
            .syncStatus(UUID.randomUUID().toString());
    }
}
