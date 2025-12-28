package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Bill;
import com.atparui.rmsservice.repository.rowmapper.BillRowMapper;
import com.atparui.rmsservice.repository.rowmapper.BranchRowMapper;
import com.atparui.rmsservice.repository.rowmapper.CustomerRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Bill entity.
 */
@SuppressWarnings("unused")
class BillRepositoryInternalImpl extends SimpleR2dbcRepository<Bill, UUID> implements BillRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrderRowMapper orderMapper;
    private final BranchRowMapper branchMapper;
    private final CustomerRowMapper customerMapper;
    private final BillRowMapper billMapper;

    private static final Table entityTable = Table.aliased("bill", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");
    private static final Table branchTable = Table.aliased("branch", "branch");
    private static final Table customerTable = Table.aliased("customer", "customer");

    public BillRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        BranchRowMapper branchMapper,
        CustomerRowMapper customerMapper,
        BillRowMapper billMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Bill.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.branchMapper = branchMapper;
        this.customerMapper = customerMapper;
        this.billMapper = billMapper;
    }

    @Override
    public Flux<Bill> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Bill> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BillSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        columns.addAll(BranchSqlHelper.getColumns(branchTable, "branch"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable))
            .leftOuterJoin(branchTable)
            .on(Column.create("branch_id", entityTable))
            .equals(Column.create("id", branchTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Bill.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Bill> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Bill> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private Bill process(Row row, RowMetadata metadata) {
        Bill entity = billMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        entity.setBranch(branchMapper.apply(row, "branch"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends Bill> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
