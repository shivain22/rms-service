package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.Discount;
import com.atparui.rmsservice.domain.Restaurant;
import com.atparui.rmsservice.service.dto.DiscountDTO;
import com.atparui.rmsservice.service.dto.RestaurantDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Discount} and its DTO {@link DiscountDTO}.
 */
@Mapper(componentModel = "spring")
public interface DiscountMapper extends EntityMapper<DiscountDTO, Discount> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    DiscountDTO toDto(Discount s);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
