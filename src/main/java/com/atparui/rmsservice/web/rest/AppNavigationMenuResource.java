package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.security.SecurityUtils;
import com.atparui.rmsservice.service.AppNavigationMenuService;
import com.atparui.rmsservice.service.dto.AppNavigationMenuResponseDTO;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing application navigation menus (CMS)
 * This is for the application's navigation menu, not the restaurant menu
 */
@RestController
@RequestMapping("/api/app-navigation-menus")
public class AppNavigationMenuResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppNavigationMenuResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppNavigationMenuService appNavigationMenuService;

    public AppNavigationMenuResource(AppNavigationMenuService appNavigationMenuService) {
        this.appNavigationMenuService = appNavigationMenuService;
    }

    /**
     * {@code GET /api/app-navigation-menus} : Get application navigation menu based on user roles
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and navigation menu response DTO
     */
    @GetMapping("")
    public Mono<ResponseEntity<AppNavigationMenuResponseDTO>> getNavigationMenu() {
        LOG.debug("REST request to get application navigation menu");
        return SecurityUtils.getCurrentUserRoles()
            .collectList()
            .flatMap(roles -> {
                if (roles.isEmpty()) {
                    // If no roles found, try to get from request parameter or use default
                    LOG.warn("No roles found for current user, using default roles");
                    roles = List.of("ROLE_USER", "ROLE_ANONYMOUS");
                }
                return appNavigationMenuService.getNavigationMenuByRoles(roles);
            })
            .map(result -> ResponseEntity.ok().body(result))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * {@code GET /api/app-navigation-menus/roles} : Get application navigation menu with specific roles
     *
     * @param roles comma-separated list of roles
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and navigation menu response DTO
     */
    @GetMapping("/roles")
    public Mono<ResponseEntity<AppNavigationMenuResponseDTO>> getNavigationMenuByRoles(@RequestParam(required = false) String roles) {
        LOG.debug("REST request to get application navigation menu with roles : {}", roles);
        List<String> roleList = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            // Parse comma-separated roles
            String[] roleArray = roles.split(",");
            for (String role : roleArray) {
                roleList.add(role.trim());
            }
        } else {
            // If no roles provided, try to get from security context
            return SecurityUtils.getCurrentUserRoles()
                .collectList()
                .flatMap(securityRoles -> {
                    if (securityRoles.isEmpty()) {
                        LOG.warn("No roles found, using default roles");
                        securityRoles = List.of("ROLE_USER", "ROLE_ANONYMOUS");
                    }
                    return appNavigationMenuService.getNavigationMenuByRoles(securityRoles);
                })
                .map(result -> ResponseEntity.ok().body(result))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
        }

        return appNavigationMenuService
            .getNavigationMenuByRoles(roleList)
            .map(result -> ResponseEntity.ok().body(result))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
