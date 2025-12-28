package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.Customer;
import com.atparui.rmsservice.domain.CustomerLoyalty;
import com.atparui.rmsservice.domain.Restaurant;
import com.atparui.rmsservice.service.dto.CustomerDTO;
import com.atparui.rmsservice.service.dto.CustomerLoyaltyDTO;
import com.atparui.rmsservice.service.dto.RestaurantDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerLoyalty} and its DTO {@link CustomerLoyaltyDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerLoyaltyMapper extends EntityMapper<CustomerLoyaltyDTO, CustomerLoyalty> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    CustomerLoyaltyDTO toDto(CustomerLoyalty s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
