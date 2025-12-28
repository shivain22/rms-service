package com.atparui.rmsservice.service;

import com.atparui.rmsservice.domain.AppNavigationMenu;
import com.atparui.rmsservice.domain.AppNavigationMenuItem;
import com.atparui.rmsservice.domain.AppNavigationMenuRole;
import com.atparui.rmsservice.repository.AppNavigationMenuItemRepository;
import com.atparui.rmsservice.repository.AppNavigationMenuRepository;
import com.atparui.rmsservice.repository.AppNavigationMenuRoleRepository;
import com.atparui.rmsservice.service.dto.AppNavigationMenuItemDTO;
import com.atparui.rmsservice.service.dto.AppNavigationMenuResponseDTO;
import com.atparui.rmsservice.service.mapper.AppNavigationMenuItemMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing role-based application navigation menus (CMS)
 * This is different from restaurant menu - this is the app navigation menu
 */
@Service
@Transactional
public class AppNavigationMenuService {

    private static final Logger LOG = LoggerFactory.getLogger(AppNavigationMenuService.class);

    private final AppNavigationMenuRepository appNavigationMenuRepository;
    private final AppNavigationMenuItemRepository appNavigationMenuItemRepository;
    private final AppNavigationMenuRoleRepository appNavigationMenuRoleRepository;
    private final AppNavigationMenuItemMapper appNavigationMenuItemMapper;

    public AppNavigationMenuService(
        AppNavigationMenuRepository appNavigationMenuRepository,
        AppNavigationMenuItemRepository appNavigationMenuItemRepository,
        AppNavigationMenuRoleRepository appNavigationMenuRoleRepository,
        AppNavigationMenuItemMapper appNavigationMenuItemMapper
    ) {
        this.appNavigationMenuRepository = appNavigationMenuRepository;
        this.appNavigationMenuItemRepository = appNavigationMenuItemRepository;
        this.appNavigationMenuRoleRepository = appNavigationMenuRoleRepository;
        this.appNavigationMenuItemMapper = appNavigationMenuItemMapper;
    }

    /**
     * Get application navigation menu filtered by roles
     *
     * @param roles the list of roles to filter by
     * @return the navigation menu response DTO with menus and items
     */
    @Transactional(readOnly = true)
    public Mono<AppNavigationMenuResponseDTO> getNavigationMenuByRoles(List<String> roles) {
        LOG.debug("Request to get application navigation menu with roles {}", roles);

        AppNavigationMenuResponseDTO response = new AppNavigationMenuResponseDTO();

        // Get all active menus
        return appNavigationMenuRepository
            .findAll()
            .filter(menu -> Boolean.TRUE.equals(menu.getIsActive()))
            .collectList()
            .flatMap(menus -> {
                // Filter menus by role
                return filterMenusByRoles(menus, roles)
                    .collectList()
                    .flatMap(filteredMenus -> {
                        // For each menu, get items filtered by role
                        return getItemsForMenus(filteredMenus, roles)
                            .collectList()
                            .map(menusWithItems -> {
                                response.setMenus(menusWithItems);
                                return response;
                            });
                    });
            });
    }

    /**
     * Filter menus by roles
     */
    private Flux<AppNavigationMenu> filterMenusByRoles(List<AppNavigationMenu> menus, List<String> roles) {
        if (menus.isEmpty()) {
            return Flux.empty();
        }

        // Get all menu role mappings for the given roles
        return Flux.fromIterable(roles)
            .flatMap(role -> appNavigationMenuRoleRepository.findByRole(role))
            .collectList()
            .map(roleMappings -> {
                Set<UUID> allowedMenuIds = roleMappings
                    .stream()
                    .filter(roleMapping -> roleMapping.getAppNavigationMenuId() != null)
                    .map(AppNavigationMenuRole::getAppNavigationMenuId)
                    .collect(Collectors.toSet());

                // If no role mappings exist, return all menus (backward compatibility)
                if (allowedMenuIds.isEmpty()) {
                    return menus;
                }

                return menus.stream().filter(menu -> allowedMenuIds.contains(menu.getId())).collect(Collectors.toList());
            })
            .flatMapMany(Flux::fromIterable);
    }

    /**
     * Get items for menus filtered by roles
     */
    private Flux<AppNavigationMenuResponseDTO.AppNavigationMenuWithItemsDTO> getItemsForMenus(
        List<AppNavigationMenu> menus,
        List<String> roles
    ) {
        if (menus.isEmpty()) {
            return Flux.empty();
        }

        // Get all item role mappings for the given roles
        return Flux.fromIterable(roles)
            .flatMap(role -> appNavigationMenuRoleRepository.findByRole(role))
            .collectList()
            .flatMapMany(roleMappings -> {
                Set<UUID> allowedItemIds = roleMappings
                    .stream()
                    .filter(roleMapping -> roleMapping.getAppNavigationMenuItemId() != null)
                    .map(AppNavigationMenuRole::getAppNavigationMenuItemId)
                    .collect(Collectors.toSet());

                // Get all items
                return appNavigationMenuItemRepository
                    .findAll()
                    .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                    .collectList()
                    .flatMapMany(items -> {
                        // Filter items by role if role mappings exist
                        List<AppNavigationMenuItem> filteredItems = allowedItemIds.isEmpty()
                            ? items
                            : items.stream().filter(item -> allowedItemIds.contains(item.getId())).collect(Collectors.toList());

                        // Group items by parent menu
                        Map<UUID, List<AppNavigationMenuItem>> itemsByMenu = filteredItems
                            .stream()
                            .filter(item -> item.getParentMenuId() != null)
                            .collect(Collectors.groupingBy(AppNavigationMenuItem::getParentMenuId));

                        // Build menu DTOs with items
                        return Flux.fromIterable(menus)
                            .map(menu -> {
                                AppNavigationMenuResponseDTO.AppNavigationMenuWithItemsDTO menuDTO =
                                    new AppNavigationMenuResponseDTO.AppNavigationMenuWithItemsDTO();
                                menuDTO.setId(menu.getId());
                                menuDTO.setMenuCode(menu.getMenuCode());
                                menuDTO.setMenuName(menu.getMenuName());
                                menuDTO.setDescription(menu.getDescription());
                                menuDTO.setMenuType(menu.getMenuType());
                                menuDTO.setIcon(menu.getIcon());
                                menuDTO.setRoutePath(menu.getRoutePath());
                                menuDTO.setDisplayOrder(menu.getDisplayOrder());
                                menuDTO.setIsActive(menu.getIsActive());

                                // Get items for this menu
                                List<AppNavigationMenuItem> menuItems = itemsByMenu.getOrDefault(menu.getId(), Collections.emptyList());
                                List<AppNavigationMenuItemDTO> itemDTOs = menuItems
                                    .stream()
                                    .sorted(
                                        Comparator.comparing(
                                            AppNavigationMenuItem::getDisplayOrder,
                                            Comparator.nullsLast(Comparator.naturalOrder())
                                        )
                                    )
                                    .map(appNavigationMenuItemMapper::toDto)
                                    .collect(Collectors.toList());
                                menuDTO.setItems(itemDTOs);

                                return menuDTO;
                            })
                            .filter(menuDTO -> !menuDTO.getItems().isEmpty()) // Only return menus with items
                            .collectList()
                            .map(menuList -> {
                                menuList.sort(
                                    Comparator.comparing(
                                        AppNavigationMenuResponseDTO.AppNavigationMenuWithItemsDTO::getDisplayOrder,
                                        Comparator.nullsLast(Comparator.naturalOrder())
                                    )
                                );
                                return menuList;
                            })
                            .flatMapMany(Flux::fromIterable);
                    });
            });
    }
}
