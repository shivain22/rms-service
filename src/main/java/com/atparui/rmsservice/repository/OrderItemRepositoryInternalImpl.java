package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.OrderItem;
import com.atparui.rmsservice.repository.rowmapper.MenuItemRowMapper;
import com.atparui.rmsservice.repository.rowmapper.MenuItemVariantRowMapper;
import com.atparui.rmsservice.repository.rowmapper.OrderItemRowMapper;
import com.atparui.rmsservice.repository.rowmapper.OrderRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the OrderItem entity.
 */
@SuppressWarnings("unused")
class OrderItemRepositoryInternalImpl extends SimpleR2dbcRepository<OrderItem, UUID> implements OrderItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrderRowMapper orderMapper;
    private final MenuItemRowMapper menuitemMapper;
    private final MenuItemVariantRowMapper menuitemvariantMapper;
    private final OrderItemRowMapper orderitemMapper;

    private static final Table entityTable = Table.aliased("order_item", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");
    private static final Table menuItemTable = Table.aliased("menu_item", "menuItem");
    private static final Table menuItemVariantTable = Table.aliased("menu_item_variant", "menuItemVariant");

    public OrderItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        MenuItemRowMapper menuitemMapper,
        MenuItemVariantRowMapper menuitemvariantMapper,
        OrderItemRowMapper orderitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OrderItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.menuitemMapper = menuitemMapper;
        this.menuitemvariantMapper = menuitemvariantMapper;
        this.orderitemMapper = orderitemMapper;
    }

    @Override
    public Flux<OrderItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OrderItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrderItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        columns.addAll(MenuItemSqlHelper.getColumns(menuItemTable, "menuItem"));
        columns.addAll(MenuItemVariantSqlHelper.getColumns(menuItemVariantTable, "menuItemVariant"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable))
            .leftOuterJoin(menuItemTable)
            .on(Column.create("menu_item_id", entityTable))
            .equals(Column.create("id", menuItemTable))
            .leftOuterJoin(menuItemVariantTable)
            .on(Column.create("menu_item_variant_id", entityTable))
            .equals(Column.create("id", menuItemVariantTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, OrderItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<OrderItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OrderItem> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private OrderItem process(Row row, RowMetadata metadata) {
        OrderItem entity = orderitemMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        entity.setMenuItem(menuitemMapper.apply(row, "menuItem"));
        entity.setMenuItemVariant(menuitemvariantMapper.apply(row, "menuItemVariant"));
        return entity;
    }

    @Override
    public <S extends OrderItem> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
