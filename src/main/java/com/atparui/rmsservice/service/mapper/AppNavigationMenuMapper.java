package com.atparui.rmsservice.service.mapper;

import com.atparui.rmsservice.domain.AppNavigationMenu;
import com.atparui.rmsservice.service.dto.AppNavigationMenuDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppNavigationMenu} and its DTO {@link AppNavigationMenuDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppNavigationMenuMapper extends EntityMapper<AppNavigationMenuDTO, AppNavigationMenu> {}
