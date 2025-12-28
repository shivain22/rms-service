package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.Branch;
import com.atparui.rmsservice.domain.BranchTable;
import com.atparui.rmsservice.domain.Customer;
import com.atparui.rmsservice.domain.Order;
import com.atparui.rmsservice.domain.RmsUser;
import com.atparui.rmsservice.service.dto.BranchDTO;
import com.atparui.rmsservice.service.dto.BranchTableDTO;
import com.atparui.rmsservice.service.dto.CustomerDTO;
import com.atparui.rmsservice.service.dto.OrderDTO;
import com.atparui.rmsservice.service.dto.RmsUserDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "branch", source = "branch", qualifiedByName = "branchId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    @Mapping(target = "user", source = "user", qualifiedByName = "rmsUserId")
    @Mapping(target = "branchTable", source = "branchTable", qualifiedByName = "branchTableId")
    OrderDTO toDto(Order s);

    @Named("branchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BranchDTO toDtoBranchId(Branch branch);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("rmsUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RmsUserDTO toDtoRmsUserId(RmsUser rmsUser);

    @Named("branchTableId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BranchTableDTO toDtoBranchTableId(BranchTable branchTable);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
