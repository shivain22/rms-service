package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.BranchTable;
import com.atparui.rmsservice.repository.rowmapper.BranchRowMapper;
import com.atparui.rmsservice.repository.rowmapper.BranchTableRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the BranchTable entity.
 */
@SuppressWarnings("unused")
class BranchTableRepositoryInternalImpl extends SimpleR2dbcRepository<BranchTable, UUID> implements BranchTableRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BranchRowMapper branchMapper;
    private final BranchTableRowMapper branchtableMapper;

    private static final Table entityTable = Table.aliased("branch_table", EntityManager.ENTITY_ALIAS);
    private static final Table branchTable = Table.aliased("branch", "branch");

    public BranchTableRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BranchRowMapper branchMapper,
        BranchTableRowMapper branchtableMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(BranchTable.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.branchMapper = branchMapper;
        this.branchtableMapper = branchtableMapper;
    }

    @Override
    public Flux<BranchTable> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<BranchTable> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BranchTableSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BranchSqlHelper.getColumns(branchTable, "branch"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(branchTable)
            .on(Column.create("branch_id", entityTable))
            .equals(Column.create("id", branchTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, BranchTable.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<BranchTable> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<BranchTable> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private BranchTable process(Row row, RowMetadata metadata) {
        BranchTable entity = branchtableMapper.apply(row, "e");
        entity.setBranch(branchMapper.apply(row, "branch"));
        return entity;
    }

    @Override
    public <S extends BranchTable> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
