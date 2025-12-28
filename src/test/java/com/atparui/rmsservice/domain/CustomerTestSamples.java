package com.atparui.rmsservice.domain;

import java.util.UUID;

public class CustomerTestSamples {

    public static Customer getCustomerSample1() {
        return new Customer()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .customerCode("customerCode1")
            .phone("phone1")
            .email("email1")
            .firstName("firstName1")
            .lastName("lastName1")
            .addressLine1("addressLine11")
            .addressLine2("addressLine21")
            .city("city1")
            .state("state1")
            .country("country1")
            .postalCode("postalCode1");
    }

    public static Customer getCustomerSample2() {
        return new Customer()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .customerCode("customerCode2")
            .phone("phone2")
            .email("email2")
            .firstName("firstName2")
            .lastName("lastName2")
            .addressLine1("addressLine12")
            .addressLine2("addressLine22")
            .city("city2")
            .state("state2")
            .country("country2")
            .postalCode("postalCode2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(UUID.randomUUID())
            .customerCode(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .addressLine1(UUID.randomUUID().toString())
            .addressLine2(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString())
            .postalCode(UUID.randomUUID().toString());
    }
}
