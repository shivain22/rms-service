package com.atparui.rmsservice.domain;

import java.util.UUID;

public class ShiftTestSamples {

    public static Shift getShiftSample1() {
        return new Shift().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).shiftName("shiftName1");
    }

    public static Shift getShiftSample2() {
        return new Shift().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).shiftName("shiftName2");
    }

    public static Shift getShiftRandomSampleGenerator() {
        return new Shift().id(UUID.randomUUID()).shiftName(UUID.randomUUID().toString());
    }
}
