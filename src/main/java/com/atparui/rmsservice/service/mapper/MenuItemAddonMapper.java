package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.MenuItem;
import com.atparui.rmsservice.domain.MenuItemAddon;
import com.atparui.rmsservice.service.dto.MenuItemAddonDTO;
import com.atparui.rmsservice.service.dto.MenuItemDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuItemAddon} and its DTO {@link MenuItemAddonDTO}.
 */
@Mapper(componentModel = "spring")
public interface MenuItemAddonMapper extends EntityMapper<MenuItemAddonDTO, MenuItemAddon> {
    @Mapping(target = "menuItem", source = "menuItem", qualifiedByName = "menuItemId")
    MenuItemAddonDTO toDto(MenuItemAddon s);

    @Named("menuItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MenuItemDTO toDtoMenuItemId(MenuItem menuItem);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
