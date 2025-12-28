package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Customer;
import com.atparui.rmsservice.repository.rowmapper.CustomerRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Customer entity.
 */
@SuppressWarnings("unused")
class CustomerRepositoryInternalImpl extends SimpleR2dbcRepository<Customer, UUID> implements CustomerRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RmsUserRowMapper rmsuserMapper;
    private final CustomerRowMapper customerMapper;

    private static final Table entityTable = Table.aliased("customer", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("rms_user", "e_user");

    public CustomerRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RmsUserRowMapper rmsuserMapper,
        CustomerRowMapper customerMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Customer.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rmsuserMapper = rmsuserMapper;
        this.customerMapper = customerMapper;
    }

    @Override
    public Flux<Customer> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Customer> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CustomerSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RmsUserSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Customer.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Customer> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Customer> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private Customer process(Row row, RowMetadata metadata) {
        Customer entity = customerMapper.apply(row, "e");
        entity.setUser(rmsuserMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends Customer> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
