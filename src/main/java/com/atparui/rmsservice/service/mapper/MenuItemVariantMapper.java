package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.MenuItem;
import com.atparui.rmsservice.domain.MenuItemVariant;
import com.atparui.rmsservice.service.dto.MenuItemDTO;
import com.atparui.rmsservice.service.dto.MenuItemVariantDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuItemVariant} and its DTO {@link MenuItemVariantDTO}.
 */
@Mapper(componentModel = "spring")
public interface MenuItemVariantMapper extends EntityMapper<MenuItemVariantDTO, MenuItemVariant> {
    @Mapping(target = "menuItem", source = "menuItem", qualifiedByName = "menuItemId")
    MenuItemVariantDTO toDto(MenuItemVariant s);

    @Named("menuItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MenuItemDTO toDtoMenuItemId(MenuItem menuItem);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
