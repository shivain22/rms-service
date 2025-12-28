package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.Branch;
import com.atparui.rmsservice.domain.Shift;
import com.atparui.rmsservice.service.dto.BranchDTO;
import com.atparui.rmsservice.service.dto.ShiftDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shift} and its DTO {@link ShiftDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShiftMapper extends EntityMapper<ShiftDTO, Shift> {
    @Mapping(target = "branch", source = "branch", qualifiedByName = "branchId")
    ShiftDTO toDto(Shift s);

    @Named("branchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BranchDTO toDtoBranchId(Branch branch);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
