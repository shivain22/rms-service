package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.InventoryRepository;
import com.atparui.rmsservice.service.InventoryService;
import com.atparui.rmsservice.service.dto.InventoryDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.Inventory}.
 */
@RestController
@RequestMapping("/api/inventories")
public class InventoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryResource.class);

    private static final String ENTITY_NAME = "rmsserviceInventory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InventoryService inventoryService;

    private final InventoryRepository inventoryRepository;

    public InventoryResource(InventoryService inventoryService, InventoryRepository inventoryRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * {@code POST  /inventories} : Create a new inventory.
     *
     * @param inventoryDTO the inventoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inventoryDTO, or with status {@code 400 (Bad Request)} if the inventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InventoryDTO>> createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) throws URISyntaxException {
        LOG.debug("REST request to save Inventory : {}", inventoryDTO);
        if (inventoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new inventory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        inventoryDTO.setId(UUID.randomUUID());
        return inventoryService
            .save(inventoryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/inventories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /inventories/:id} : Updates an existing inventory.
     *
     * @param id the id of the inventoryDTO to save.
     * @param inventoryDTO the inventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryDTO,
     * or with status {@code 400 (Bad Request)} if the inventoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InventoryDTO>> updateInventory(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody InventoryDTO inventoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Inventory : {}, {}", id, inventoryDTO);
        if (inventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return inventoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return inventoryService
                    .update(inventoryDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /inventories/:id} : Partial updates given fields of an existing inventory, field will ignore if it is null
     *
     * @param id the id of the inventoryDTO to save.
     * @param inventoryDTO the inventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryDTO,
     * or with status {@code 400 (Bad Request)} if the inventoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the inventoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the inventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InventoryDTO>> partialUpdateInventory(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody InventoryDTO inventoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Inventory partially : {}, {}", id, inventoryDTO);
        if (inventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return inventoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<InventoryDTO> result = inventoryService.partialUpdate(inventoryDTO);

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
     * {@code GET  /inventories} : get all the inventories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inventories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<InventoryDTO>> getAllInventories() {
        LOG.debug("REST request to get all Inventories");
        return inventoryService.findAll().collectList();
    }

    /**
     * {@code GET  /inventories} : get all the inventories as a stream.
     * @return the {@link Flux} of inventories.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<InventoryDTO> getAllInventoriesAsStream() {
        LOG.debug("REST request to get all Inventories as a stream");
        return inventoryService.findAll();
    }

    /**
     * {@code GET  /inventories/:id} : get the "id" inventory.
     *
     * @param id the id of the inventoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inventoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InventoryDTO>> getInventory(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get Inventory : {}", id);
        Mono<InventoryDTO> inventoryDTO = inventoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inventoryDTO);
    }

    /**
     * {@code DELETE  /inventories/:id} : delete the "id" inventory.
     *
     * @param id the id of the inventoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInventory(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete Inventory : {}", id);
        return inventoryService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    // jhipster-needle-rest-add-get-method - JHipster will add get methods here

    /**
     * {@code GET /api/inventories/branch/{branchId}/low-stock} : Get low stock items
     *
     * @param branchId the branch ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of low stock items
     */
    @GetMapping("/branch/{branchId}/low-stock")
    public Mono<ResponseEntity<List<InventoryDTO>>> getLowStockItems(@PathVariable UUID branchId) {
        LOG.debug("REST request to get low stock items for branch : {}", branchId);
        return inventoryService.findLowStockByBranchId(branchId).collectList().map(result -> ResponseEntity.ok().body(result));
    }

    // jhipster-needle-rest-add-post-method - JHipster will add post methods here

    /**
     * {@code POST /api/inventories/{id}/adjust} : Adjust inventory stock
     *
     * @param id the id of the inventory
     * @param request the stock adjustment request
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and updated inventory DTO
     */
    @PostMapping("/{id}/adjust")
    public Mono<ResponseEntity<InventoryDTO>> adjustStock(
        @PathVariable UUID id,
        @Valid @RequestBody com.atparui.rmsservice.service.dto.StockAdjustmentRequestDTO request
    ) {
        LOG.debug("REST request to adjust inventory stock : {} - {}", id, request);
        return inventoryService.adjustStock(id, request).map(result -> ResponseEntity.ok().body(result));
    }

    // jhipster-needle-rest-add-put-method - JHipster will add put methods here

    /**
     * {@code PUT /api/inventories/{id}/stock} : Update inventory stock level
     *
     * @param id the id of the inventory
     * @param request the stock update request
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and updated inventory DTO
     */
    @PutMapping("/{id}/stock")
    public Mono<ResponseEntity<InventoryDTO>> updateStock(
        @PathVariable UUID id,
        @Valid @RequestBody com.atparui.rmsservice.service.dto.StockUpdateRequestDTO request
    ) {
        LOG.debug("REST request to update inventory stock : {} - {}", id, request);
        return inventoryService.updateStock(id, request).map(result -> ResponseEntity.ok().body(result));
    }
}
