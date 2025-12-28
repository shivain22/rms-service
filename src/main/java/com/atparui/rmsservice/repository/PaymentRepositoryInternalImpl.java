package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Payment;
import com.atparui.rmsservice.repository.rowmapper.BillRowMapper;
import com.atparui.rmsservice.repository.rowmapper.PaymentMethodRowMapper;
import com.atparui.rmsservice.repository.rowmapper.PaymentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Payment entity.
 */
@SuppressWarnings("unused")
class PaymentRepositoryInternalImpl extends SimpleR2dbcRepository<Payment, UUID> implements PaymentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BillRowMapper billMapper;
    private final PaymentMethodRowMapper paymentmethodMapper;
    private final PaymentRowMapper paymentMapper;

    private static final Table entityTable = Table.aliased("payment", EntityManager.ENTITY_ALIAS);
    private static final Table billTable = Table.aliased("bill", "bill");
    private static final Table paymentMethodTable = Table.aliased("payment_method", "paymentMethod");

    public PaymentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BillRowMapper billMapper,
        PaymentMethodRowMapper paymentmethodMapper,
        PaymentRowMapper paymentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Payment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.billMapper = billMapper;
        this.paymentmethodMapper = paymentmethodMapper;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Flux<Payment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Payment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PaymentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BillSqlHelper.getColumns(billTable, "bill"));
        columns.addAll(PaymentMethodSqlHelper.getColumns(paymentMethodTable, "paymentMethod"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(billTable)
            .on(Column.create("bill_id", entityTable))
            .equals(Column.create("id", billTable))
            .leftOuterJoin(paymentMethodTable)
            .on(Column.create("payment_method_id", entityTable))
            .equals(Column.create("id", paymentMethodTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Payment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Payment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Payment> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private Payment process(Row row, RowMetadata metadata) {
        Payment entity = paymentMapper.apply(row, "e");
        entity.setBill(billMapper.apply(row, "bill"));
        entity.setPaymentMethod(paymentmethodMapper.apply(row, "paymentMethod"));
        return entity;
    }

    @Override
    public <S extends Payment> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
