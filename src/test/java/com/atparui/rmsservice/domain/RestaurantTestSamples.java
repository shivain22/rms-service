package com.atparui.rmsservice.domain;

import java.util.UUID;

public class RestaurantTestSamples {

    public static Restaurant getRestaurantSample1() {
        return new Restaurant()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .code("code1")
            .logoUrl("logoUrl1")
            .contactEmail("contactEmail1")
            .contactPhone("contactPhone1")
            .addressLine1("addressLine11")
            .addressLine2("addressLine21")
            .city("city1")
            .state("state1")
            .country("country1")
            .postalCode("postalCode1")
            .timezone("timezone1");
    }

    public static Restaurant getRestaurantSample2() {
        return new Restaurant()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .code("code2")
            .logoUrl("logoUrl2")
            .contactEmail("contactEmail2")
            .contactPhone("contactPhone2")
            .addressLine1("addressLine12")
            .addressLine2("addressLine22")
            .city("city2")
            .state("state2")
            .country("country2")
            .postalCode("postalCode2")
            .timezone("timezone2");
    }

    public static Restaurant getRestaurantRandomSampleGenerator() {
        return new Restaurant()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .logoUrl(UUID.randomUUID().toString())
            .contactEmail(UUID.randomUUID().toString())
            .contactPhone(UUID.randomUUID().toString())
            .addressLine1(UUID.randomUUID().toString())
            .addressLine2(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString())
            .postalCode(UUID.randomUUID().toString())
            .timezone(UUID.randomUUID().toString());
    }
}
