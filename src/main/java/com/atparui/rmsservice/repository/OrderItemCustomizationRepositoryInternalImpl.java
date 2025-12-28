package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.OrderItemCustomization;
import com.atparui.rmsservice.repository.rowmapper.MenuItemAddonRowMapper;
import com.atparui.rmsservice.repository.rowmapper.OrderItemCustomizationRowMapper;
import com.atparui.rmsservice.repository.rowmapper.OrderItemRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the OrderItemCustomization entity.
 */
@SuppressWarnings("unused")
class OrderItemCustomizationRepositoryInternalImpl
    extends SimpleR2dbcRepository<OrderItemCustomization, UUID>
    implements OrderItemCustomizationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrderItemRowMapper orderitemMapper;
    private final MenuItemAddonRowMapper menuitemaddonMapper;
    private final OrderItemCustomizationRowMapper orderitemcustomizationMapper;

    private static final Table entityTable = Table.aliased("order_item_customization", EntityManager.ENTITY_ALIAS);
    private static final Table orderItemTable = Table.aliased("order_item", "orderItem");
    private static final Table menuItemAddonTable = Table.aliased("menu_item_addon", "menuItemAddon");

    public OrderItemCustomizationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderItemRowMapper orderitemMapper,
        MenuItemAddonRowMapper menuitemaddonMapper,
        OrderItemCustomizationRowMapper orderitemcustomizationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OrderItemCustomization.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.orderitemMapper = orderitemMapper;
        this.menuitemaddonMapper = menuitemaddonMapper;
        this.orderitemcustomizationMapper = orderitemcustomizationMapper;
    }

    @Override
    public Flux<OrderItemCustomization> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OrderItemCustomization> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrderItemCustomizationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderItemSqlHelper.getColumns(orderItemTable, "orderItem"));
        columns.addAll(MenuItemAddonSqlHelper.getColumns(menuItemAddonTable, "menuItemAddon"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderItemTable)
            .on(Column.create("order_item_id", entityTable))
            .equals(Column.create("id", orderItemTable))
            .leftOuterJoin(menuItemAddonTable)
            .on(Column.create("menu_item_addon_id", entityTable))
            .equals(Column.create("id", menuItemAddonTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, OrderItemCustomization.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<OrderItemCustomization> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OrderItemCustomization> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private OrderItemCustomization process(Row row, RowMetadata metadata) {
        OrderItemCustomization entity = orderitemcustomizationMapper.apply(row, "e");
        entity.setOrderItem(orderitemMapper.apply(row, "orderItem"));
        entity.setMenuItemAddon(menuitemaddonMapper.apply(row, "menuItemAddon"));
        return entity;
    }

    @Override
    public <S extends OrderItemCustomization> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
