package com.atparui.rmsservice.domain;

import java.util.UUID;

public class UserBranchRoleTestSamples {

    public static UserBranchRole getUserBranchRoleSample1() {
        return new UserBranchRole()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .role("role1")
            .assignedBy("assignedBy1")
            .revokedBy("revokedBy1");
    }

    public static UserBranchRole getUserBranchRoleSample2() {
        return new UserBranchRole()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .role("role2")
            .assignedBy("assignedBy2")
            .revokedBy("revokedBy2");
    }

    public static UserBranchRole getUserBranchRoleRandomSampleGenerator() {
        return new UserBranchRole()
            .id(UUID.randomUUID())
            .role(UUID.randomUUID().toString())
            .assignedBy(UUID.randomUUID().toString())
            .revokedBy(UUID.randomUUID().toString());
    }
}
