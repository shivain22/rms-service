package com.atparui.rmsservice.domain;

import java.util.UUID;

public class TableWaiterAssignmentTestSamples {

    public static TableWaiterAssignment getTableWaiterAssignmentSample1() {
        return new TableWaiterAssignment().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static TableWaiterAssignment getTableWaiterAssignmentSample2() {
        return new TableWaiterAssignment().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static TableWaiterAssignment getTableWaiterAssignmentRandomSampleGenerator() {
        return new TableWaiterAssignment().id(UUID.randomUUID());
    }
}
