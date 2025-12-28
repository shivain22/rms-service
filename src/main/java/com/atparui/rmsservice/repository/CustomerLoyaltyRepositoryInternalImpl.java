package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.CustomerLoyalty;
import com.atparui.rmsservice.repository.rowmapper.CustomerLoyaltyRowMapper;
import com.atparui.rmsservice.repository.rowmapper.CustomerRowMapper;
import com.atparui.rmsservice.repository.rowmapper.RestaurantRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the CustomerLoyalty entity.
 */
@SuppressWarnings("unused")
class CustomerLoyaltyRepositoryInternalImpl
    extends SimpleR2dbcRepository<CustomerLoyalty, UUID>
    implements CustomerLoyaltyRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final RestaurantRowMapper restaurantMapper;
    private final CustomerLoyaltyRowMapper customerloyaltyMapper;

    private static final Table entityTable = Table.aliased("customer_loyalty", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");
    private static final Table restaurantTable = Table.aliased("restaurant", "restaurant");

    public CustomerLoyaltyRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        RestaurantRowMapper restaurantMapper,
        CustomerLoyaltyRowMapper customerloyaltyMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(CustomerLoyalty.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.restaurantMapper = restaurantMapper;
        this.customerloyaltyMapper = customerloyaltyMapper;
    }

    @Override
    public Flux<CustomerLoyalty> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<CustomerLoyalty> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CustomerLoyaltySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        columns.addAll(RestaurantSqlHelper.getColumns(restaurantTable, "restaurant"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable))
            .leftOuterJoin(restaurantTable)
            .on(Column.create("restaurant_id", entityTable))
            .equals(Column.create("id", restaurantTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, CustomerLoyalty.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<CustomerLoyalty> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<CustomerLoyalty> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private CustomerLoyalty process(Row row, RowMetadata metadata) {
        CustomerLoyalty entity = customerloyaltyMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        entity.setRestaurant(restaurantMapper.apply(row, "restaurant"));
        return entity;
    }

    @Override
    public <S extends CustomerLoyalty> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
