package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Inventory;
import com.atparui.rmsservice.repository.rowmapper.BranchRowMapper;
import com.atparui.rmsservice.repository.rowmapper.InventoryRowMapper;
import com.atparui.rmsservice.repository.rowmapper.MenuItemRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Inventory entity.
 */
@SuppressWarnings("unused")
class InventoryRepositoryInternalImpl extends SimpleR2dbcRepository<Inventory, UUID> implements InventoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BranchRowMapper branchMapper;
    private final MenuItemRowMapper menuitemMapper;
    private final InventoryRowMapper inventoryMapper;

    private static final Table entityTable = Table.aliased("inventory", EntityManager.ENTITY_ALIAS);
    private static final Table branchTable = Table.aliased("branch", "branch");
    private static final Table menuItemTable = Table.aliased("menu_item", "menuItem");

    public InventoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BranchRowMapper branchMapper,
        MenuItemRowMapper menuitemMapper,
        InventoryRowMapper inventoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Inventory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.branchMapper = branchMapper;
        this.menuitemMapper = menuitemMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public Flux<Inventory> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Inventory> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = InventorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BranchSqlHelper.getColumns(branchTable, "branch"));
        columns.addAll(MenuItemSqlHelper.getColumns(menuItemTable, "menuItem"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(branchTable)
            .on(Column.create("branch_id", entityTable))
            .equals(Column.create("id", branchTable))
            .leftOuterJoin(menuItemTable)
            .on(Column.create("menu_item_id", entityTable))
            .equals(Column.create("id", menuItemTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Inventory.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Inventory> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Inventory> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private Inventory process(Row row, RowMetadata metadata) {
        Inventory entity = inventoryMapper.apply(row, "e");
        entity.setBranch(branchMapper.apply(row, "branch"));
        entity.setMenuItem(menuitemMapper.apply(row, "menuItem"));
        return entity;
    }

    @Override
    public <S extends Inventory> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
