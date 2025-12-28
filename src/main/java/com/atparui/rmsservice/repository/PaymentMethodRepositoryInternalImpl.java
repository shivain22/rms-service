package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.PaymentMethod;
import com.atparui.rmsservice.repository.rowmapper.PaymentMethodRowMapper;
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
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the PaymentMethod entity.
 */
@SuppressWarnings("unused")
class PaymentMethodRepositoryInternalImpl extends SimpleR2dbcRepository<PaymentMethod, UUID> implements PaymentMethodRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PaymentMethodRowMapper paymentmethodMapper;

    private static final Table entityTable = Table.aliased("payment_method", EntityManager.ENTITY_ALIAS);

    public PaymentMethodRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PaymentMethodRowMapper paymentmethodMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(PaymentMethod.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.paymentmethodMapper = paymentmethodMapper;
    }

    @Override
    public Flux<PaymentMethod> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<PaymentMethod> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PaymentMethodSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, PaymentMethod.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<PaymentMethod> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<PaymentMethod> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private PaymentMethod process(Row row, RowMetadata metadata) {
        PaymentMethod entity = paymentmethodMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends PaymentMethod> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
