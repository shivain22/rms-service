package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.MenuItem;
import com.atparui.rmsservice.domain.MenuItemVariant;
import com.atparui.rmsservice.domain.Order;
import com.atparui.rmsservice.domain.OrderItem;
import com.atparui.rmsservice.service.dto.MenuItemDTO;
import com.atparui.rmsservice.service.dto.MenuItemVariantDTO;
import com.atparui.rmsservice.service.dto.OrderDTO;
import com.atparui.rmsservice.service.dto.OrderItemDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    @Mapping(target = "menuItem", source = "menuItem", qualifiedByName = "menuItemId")
    @Mapping(target = "menuItemVariant", source = "menuItemVariant", qualifiedByName = "menuItemVariantId")
    OrderItemDTO toDto(OrderItem s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @Named("menuItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MenuItemDTO toDtoMenuItemId(MenuItem menuItem);

    @Named("menuItemVariantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MenuItemVariantDTO toDtoMenuItemVariantId(MenuItemVariant menuItemVariant);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
