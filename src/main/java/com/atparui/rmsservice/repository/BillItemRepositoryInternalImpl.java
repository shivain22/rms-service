package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.BillItem;
import com.atparui.rmsservice.repository.rowmapper.BillItemRowMapper;
import com.atparui.rmsservice.repository.rowmapper.BillRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the BillItem entity.
 */
@SuppressWarnings("unused")
class BillItemRepositoryInternalImpl extends SimpleR2dbcRepository<BillItem, UUID> implements BillItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BillRowMapper billMapper;
    private final OrderItemRowMapper orderitemMapper;
    private final BillItemRowMapper billitemMapper;

    private static final Table entityTable = Table.aliased("bill_item", EntityManager.ENTITY_ALIAS);
    private static final Table billTable = Table.aliased("bill", "bill");
    private static final Table orderItemTable = Table.aliased("order_item", "orderItem");

    public BillItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BillRowMapper billMapper,
        OrderItemRowMapper orderitemMapper,
        BillItemRowMapper billitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(BillItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.billMapper = billMapper;
        this.orderitemMapper = orderitemMapper;
        this.billitemMapper = billitemMapper;
    }

    @Override
    public Flux<BillItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<BillItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BillItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BillSqlHelper.getColumns(billTable, "bill"));
        columns.addAll(OrderItemSqlHelper.getColumns(orderItemTable, "orderItem"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(billTable)
            .on(Column.create("bill_id", entityTable))
            .equals(Column.create("id", billTable))
            .leftOuterJoin(orderItemTable)
            .on(Column.create("order_item_id", entityTable))
            .equals(Column.create("id", orderItemTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, BillItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<BillItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<BillItem> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private BillItem process(Row row, RowMetadata metadata) {
        BillItem entity = billitemMapper.apply(row, "e");
        entity.setBill(billMapper.apply(row, "bill"));
        entity.setOrderItem(orderitemMapper.apply(row, "orderItem"));
        return entity;
    }

    @Override
    public <S extends BillItem> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
