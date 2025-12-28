package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.UserSyncLogRepository;
import com.atparui.rmsservice.service.UserSyncLogService;
import com.atparui.rmsservice.service.dto.UserSyncLogDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.atparui.rmsservice.domain.UserSyncLog}.
 */
@RestController
@RequestMapping("/api/user-sync-logs")
public class UserSyncLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserSyncLogResource.class);

    private static final String ENTITY_NAME = "rmsserviceUserSyncLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserSyncLogService userSyncLogService;

    private final UserSyncLogRepository userSyncLogRepository;

    public UserSyncLogResource(UserSyncLogService userSyncLogService, UserSyncLogRepository userSyncLogRepository) {
        this.userSyncLogService = userSyncLogService;
        this.userSyncLogRepository = userSyncLogRepository;
    }

    /**
     * {@code POST  /user-sync-logs} : Create a new userSyncLog.
     *
     * @param userSyncLogDTO the userSyncLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userSyncLogDTO, or with status {@code 400 (Bad Request)} if the userSyncLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<UserSyncLogDTO>> createUserSyncLog(@Valid @RequestBody UserSyncLogDTO userSyncLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UserSyncLog : {}", userSyncLogDTO);
        if (userSyncLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new userSyncLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userSyncLogDTO.setId(UUID.randomUUID());
        return userSyncLogService
            .save(userSyncLogDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/user-sync-logs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-sync-logs/:id} : Updates an existing userSyncLog.
     *
     * @param id the id of the userSyncLogDTO to save.
     * @param userSyncLogDTO the userSyncLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSyncLogDTO,
     * or with status {@code 400 (Bad Request)} if the userSyncLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userSyncLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserSyncLogDTO>> updateUserSyncLog(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody UserSyncLogDTO userSyncLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserSyncLog : {}, {}", id, userSyncLogDTO);
        if (userSyncLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSyncLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userSyncLogRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userSyncLogService
                    .update(userSyncLogDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /user-sync-logs/:id} : Partial updates given fields of an existing userSyncLog, field will ignore if it is null
     *
     * @param id the id of the userSyncLogDTO to save.
     * @param userSyncLogDTO the userSyncLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSyncLogDTO,
     * or with status {@code 400 (Bad Request)} if the userSyncLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userSyncLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userSyncLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserSyncLogDTO>> partialUpdateUserSyncLog(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody UserSyncLogDTO userSyncLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserSyncLog partially : {}, {}", id, userSyncLogDTO);
        if (userSyncLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSyncLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userSyncLogRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserSyncLogDTO> result = userSyncLogService.partialUpdate(userSyncLogDTO);

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
     * {@code GET  /user-sync-logs} : get all the userSyncLogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userSyncLogs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<UserSyncLogDTO>> getAllUserSyncLogs() {
        LOG.debug("REST request to get all UserSyncLogs");
        return userSyncLogService.findAll().collectList();
    }

    /**
     * {@code GET  /user-sync-logs} : get all the userSyncLogs as a stream.
     * @return the {@link Flux} of userSyncLogs.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserSyncLogDTO> getAllUserSyncLogsAsStream() {
        LOG.debug("REST request to get all UserSyncLogs as a stream");
        return userSyncLogService.findAll();
    }

    /**
     * {@code GET  /user-sync-logs/:id} : get the "id" userSyncLog.
     *
     * @param id the id of the userSyncLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userSyncLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserSyncLogDTO>> getUserSyncLog(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get UserSyncLog : {}", id);
        Mono<UserSyncLogDTO> userSyncLogDTO = userSyncLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userSyncLogDTO);
    }

    /**
     * {@code DELETE  /user-sync-logs/:id} : delete the "id" userSyncLog.
     *
     * @param id the id of the userSyncLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUserSyncLog(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete UserSyncLog : {}", id);
        return userSyncLogService
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
