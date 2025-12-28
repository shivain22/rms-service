package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.Branch;
import com.atparui.rmsservice.domain.Restaurant;
import com.atparui.rmsservice.service.dto.BranchDTO;
import com.atparui.rmsservice.service.dto.RestaurantDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Branch} and its DTO {@link BranchDTO}.
 */
@Mapper(componentModel = "spring")
public interface BranchMapper extends EntityMapper<BranchDTO, Branch> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    BranchDTO toDto(Branch s);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
