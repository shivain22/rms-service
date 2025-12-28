package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Order;
import com.atparui.rmsservice.repository.rowmapper.BranchRowMapper;
import com.atparui.rmsservice.repository.rowmapper.BranchTableRowMapper;
import com.atparui.rmsservice.repository.rowmapper.CustomerRowMapper;
import com.atparui.rmsservice.repository.rowmapper.OrderRowMapper;
import com.atparui.rmsservice.repository.rowmapper.RmsUserRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Order entity.
 */
@SuppressWarnings("unused")
class OrderRepositoryInternalImpl extends SimpleR2dbcRepository<Order, UUID> implements OrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BranchRowMapper branchMapper;
    private final CustomerRowMapper customerMapper;
    private final RmsUserRowMapper rmsuserMapper;
    private final BranchTableRowMapper branchtableMapper;
    private final OrderRowMapper orderMapper;

    private static final Table entityTable = Table.aliased("jhi_order", EntityManager.ENTITY_ALIAS);
    private static final Table branchTable = Table.aliased("branch", "branch");
    private static final Table customerTable = Table.aliased("customer", "customer");
    private static final Table userTable = Table.aliased("rms_user", "e_user");
    private static final Table branchTableTable = Table.aliased("branch_table", "branchTable");

    public OrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BranchRowMapper branchMapper,
        CustomerRowMapper customerMapper,
        RmsUserRowMapper rmsuserMapper,
        BranchTableRowMapper branchtableMapper,
        OrderRowMapper orderMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Order.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.branchMapper = branchMapper;
        this.customerMapper = customerMapper;
        this.rmsuserMapper = rmsuserMapper;
        this.branchtableMapper = branchtableMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public Flux<Order> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Order> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BranchSqlHelper.getColumns(branchTable, "branch"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        columns.addAll(RmsUserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(BranchTableSqlHelper.getColumns(branchTableTable, "branchTable"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(branchTable)
            .on(Column.create("branch_id", entityTable))
            .equals(Column.create("id", branchTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(branchTableTable)
            .on(Column.create("branch_table_id", entityTable))
            .equals(Column.create("id", branchTableTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Order.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Order> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Order> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private Order process(Row row, RowMetadata metadata) {
        Order entity = orderMapper.apply(row, "e");
        entity.setBranch(branchMapper.apply(row, "branch"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        entity.setUser(rmsuserMapper.apply(row, "user"));
        entity.setBranchTable(branchtableMapper.apply(row, "branchTable"));
        return entity;
    }

    @Override
    public <S extends Order> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
