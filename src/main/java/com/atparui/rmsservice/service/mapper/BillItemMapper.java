package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.Bill;
import com.atparui.rmsservice.domain.BillItem;
import com.atparui.rmsservice.domain.OrderItem;
import com.atparui.rmsservice.service.dto.BillDTO;
import com.atparui.rmsservice.service.dto.BillItemDTO;
import com.atparui.rmsservice.service.dto.OrderItemDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BillItem} and its DTO {@link BillItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface BillItemMapper extends EntityMapper<BillItemDTO, BillItem> {
    @Mapping(target = "bill", source = "bill", qualifiedByName = "billId")
    @Mapping(target = "orderItem", source = "orderItem", qualifiedByName = "orderItemId")
    BillItemDTO toDto(BillItem s);

    @Named("billId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BillDTO toDtoBillId(Bill bill);

    @Named("orderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderItemDTO toDtoOrderItemId(OrderItem orderItem);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
