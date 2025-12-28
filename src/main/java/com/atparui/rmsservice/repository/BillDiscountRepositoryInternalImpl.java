package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.BillDiscount;
import com.atparui.rmsservice.repository.rowmapper.BillDiscountRowMapper;
import com.atparui.rmsservice.repository.rowmapper.BillRowMapper;
import com.atparui.rmsservice.repository.rowmapper.DiscountRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the BillDiscount entity.
 */
@SuppressWarnings("unused")
class BillDiscountRepositoryInternalImpl extends SimpleR2dbcRepository<BillDiscount, UUID> implements BillDiscountRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BillRowMapper billMapper;
    private final DiscountRowMapper discountMapper;
    private final BillDiscountRowMapper billdiscountMapper;

    private static final Table entityTable = Table.aliased("bill_discount", EntityManager.ENTITY_ALIAS);
    private static final Table billTable = Table.aliased("bill", "bill");
    private static final Table discountTable = Table.aliased("discount", "discount");

    public BillDiscountRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BillRowMapper billMapper,
        DiscountRowMapper discountMapper,
        BillDiscountRowMapper billdiscountMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(BillDiscount.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.billMapper = billMapper;
        this.discountMapper = discountMapper;
        this.billdiscountMapper = billdiscountMapper;
    }

    @Override
    public Flux<BillDiscount> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<BillDiscount> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BillDiscountSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BillSqlHelper.getColumns(billTable, "bill"));
        columns.addAll(DiscountSqlHelper.getColumns(discountTable, "discount"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(billTable)
            .on(Column.create("bill_id", entityTable))
            .equals(Column.create("id", billTable))
            .leftOuterJoin(discountTable)
            .on(Column.create("discount_id", entityTable))
            .equals(Column.create("id", discountTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, BillDiscount.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<BillDiscount> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<BillDiscount> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private BillDiscount process(Row row, RowMetadata metadata) {
        BillDiscount entity = billdiscountMapper.apply(row, "e");
        entity.setBill(billMapper.apply(row, "bill"));
        entity.setDiscount(discountMapper.apply(row, "discount"));
        return entity;
    }

    @Override
    public <S extends BillDiscount> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
