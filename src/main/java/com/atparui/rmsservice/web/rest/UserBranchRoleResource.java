package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.UserBranchRoleRepository;
import com.atparui.rmsservice.service.UserBranchRoleService;
import com.atparui.rmsservice.service.dto.UserBranchRoleDTO;
import com.atparui.rmsservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.atparui.rmsservice.domain.UserBranchRole}.
 */
@RestController
@RequestMapping("/api/user-branch-roles")
public class UserBranchRoleResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserBranchRoleResource.class);

    private static final String ENTITY_NAME = "rmsserviceUserBranchRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserBranchRoleService userBranchRoleService;

    private final UserBranchRoleRepository userBranchRoleRepository;

    public UserBranchRoleResource(UserBranchRoleService userBranchRoleService, UserBranchRoleRepository userBranchRoleRepository) {
        this.userBranchRoleService = userBranchRoleService;
        this.userBranchRoleRepository = userBranchRoleRepository;
    }

    /**
     * {@code POST  /user-branch-roles} : Create a new userBranchRole.
     *
     * @param userBranchRoleDTO the userBranchRoleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userBranchRoleDTO, or with status {@code 400 (Bad Request)} if the userBranchRole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<UserBranchRoleDTO>> createUserBranchRole(@Valid @RequestBody UserBranchRoleDTO userBranchRoleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UserBranchRole : {}", userBranchRoleDTO);
        if (userBranchRoleDTO.getId() != null) {
            throw new BadRequestAlertException("A new userBranchRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userBranchRoleDTO.setId(UUID.randomUUID());
        return userBranchRoleService
            .save(userBranchRoleDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/user-branch-roles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-branch-roles/:id} : Updates an existing userBranchRole.
     *
     * @param id the id of the userBranchRoleDTO to save.
     * @param userBranchRoleDTO the userBranchRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userBranchRoleDTO,
     * or with status {@code 400 (Bad Request)} if the userBranchRoleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userBranchRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserBranchRoleDTO>> updateUserBranchRole(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody UserBranchRoleDTO userBranchRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserBranchRole : {}, {}", id, userBranchRoleDTO);
        if (userBranchRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userBranchRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userBranchRoleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userBranchRoleService
                    .update(userBranchRoleDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /user-branch-roles/:id} : Partial updates given fields of an existing userBranchRole, field will ignore if it is null
     *
     * @param id the id of the userBranchRoleDTO to save.
     * @param userBranchRoleDTO the userBranchRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userBranchRoleDTO,
     * or with status {@code 400 (Bad Request)} if the userBranchRoleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userBranchRoleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userBranchRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserBranchRoleDTO>> partialUpdateUserBranchRole(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody UserBranchRoleDTO userBranchRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserBranchRole partially : {}, {}", id, userBranchRoleDTO);
        if (userBranchRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userBranchRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userBranchRoleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserBranchRoleDTO> result = userBranchRoleService.partialUpdate(userBranchRoleDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /user-branch-roles} : get all the userBranchRoles.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userBranchRoles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<UserBranchRoleDTO>>> getAllUserBranchRoles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of UserBranchRoles");
        return userBranchRoleService
            .countAll()
            .zipWith(userBranchRoleService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /user-branch-roles/:id} : get the "id" userBranchRole.
     *
     * @param id the id of the userBranchRoleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userBranchRoleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserBranchRoleDTO>> getUserBranchRole(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get UserBranchRole : {}", id);
        Mono<UserBranchRoleDTO> userBranchRoleDTO = userBranchRoleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userBranchRoleDTO);
    }

    /**
     * {@code DELETE  /user-branch-roles/:id} : delete the "id" userBranchRole.
     *
     * @param id the id of the userBranchRoleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUserBranchRole(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete UserBranchRole : {}", id);
        return userBranchRoleService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
