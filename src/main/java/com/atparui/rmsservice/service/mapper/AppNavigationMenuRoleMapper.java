package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.AppNavigationMenu;
import com.atparui.rmsservice.domain.AppNavigationMenuItem;
import com.atparui.rmsservice.domain.AppNavigationMenuRole;
import com.atparui.rmsservice.service.dto.AppNavigationMenuDTO;
import com.atparui.rmsservice.service.dto.AppNavigationMenuItemDTO;
import com.atparui.rmsservice.service.dto.AppNavigationMenuRoleDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppNavigationMenuRole} and its DTO {@link AppNavigationMenuRoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppNavigationMenuRoleMapper extends EntityMapper<AppNavigationMenuRoleDTO, AppNavigationMenuRole> {
    @Mapping(target = "appNavigationMenu", source = "appNavigationMenu", qualifiedByName = "appNavigationMenuId")
    @Mapping(target = "appNavigationMenuItem", source = "appNavigationMenuItem", qualifiedByName = "appNavigationMenuItemId")
    AppNavigationMenuRoleDTO toDto(AppNavigationMenuRole s);

    @Named("appNavigationMenuId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppNavigationMenuDTO toDtoAppNavigationMenuId(AppNavigationMenu appNavigationMenu);

    @Named("appNavigationMenuItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppNavigationMenuItemDTO toDtoAppNavigationMenuItemId(AppNavigationMenuItem appNavigationMenuItem);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
