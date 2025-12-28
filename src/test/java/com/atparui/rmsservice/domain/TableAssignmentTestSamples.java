package com.atparui.rmsservice.domain;

import java.util.UUID;

public class TableAssignmentTestSamples {

    public static TableAssignment getTableAssignmentSample1() {
        return new TableAssignment().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static TableAssignment getTableAssignmentSample2() {
        return new TableAssignment().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static TableAssignment getTableAssignmentRandomSampleGenerator() {
        return new TableAssignment().id(UUID.randomUUID());
    }
}
