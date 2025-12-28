package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.AppNavigationMenu;
import com.atparui.rmsservice.domain.AppNavigationMenuItem;
import com.atparui.rmsservice.service.dto.AppNavigationMenuDTO;
import com.atparui.rmsservice.service.dto.AppNavigationMenuItemDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppNavigationMenuItem} and its DTO {@link AppNavigationMenuItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppNavigationMenuItemMapper extends EntityMapper<AppNavigationMenuItemDTO, AppNavigationMenuItem> {
    @Mapping(target = "parentMenu", source = "parentMenu", qualifiedByName = "appNavigationMenuId")
    AppNavigationMenuItemDTO toDto(AppNavigationMenuItem s);

    @Named("appNavigationMenuId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppNavigationMenuDTO toDtoAppNavigationMenuId(AppNavigationMenu appNavigationMenu);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
