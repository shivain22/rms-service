package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.Restaurant;
import com.atparui.rmsservice.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Restaurant} and its DTO {@link RestaurantDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantMapper extends EntityMapper<RestaurantDTO, Restaurant> {}
