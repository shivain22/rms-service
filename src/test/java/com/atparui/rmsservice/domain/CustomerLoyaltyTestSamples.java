package com.atparui.rmsservice.domain;

import java.util.UUID;

public class CustomerLoyaltyTestSamples {

    public static CustomerLoyalty getCustomerLoyaltySample1() {
        return new CustomerLoyalty().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).tier("tier1");
    }

    public static CustomerLoyalty getCustomerLoyaltySample2() {
        return new CustomerLoyalty().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).tier("tier2");
    }

    public static CustomerLoyalty getCustomerLoyaltyRandomSampleGenerator() {
        return new CustomerLoyalty().id(UUID.randomUUID()).tier(UUID.randomUUID().toString());
    }
}
