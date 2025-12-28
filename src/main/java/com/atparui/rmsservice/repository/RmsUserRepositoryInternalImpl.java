package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.RmsUser;
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
 * Spring Data R2DBC custom repository implementation for the RmsUser entity.
 */
@SuppressWarnings("unused")
class RmsUserRepositoryInternalImpl extends SimpleR2dbcRepository<RmsUser, UUID> implements RmsUserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RmsUserRowMapper rmsuserMapper;

    private static final Table entityTable = Table.aliased("rms_user", EntityManager.ENTITY_ALIAS);

    public RmsUserRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RmsUserRowMapper rmsuserMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(RmsUser.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rmsuserMapper = rmsuserMapper;
    }

    @Override
    public Flux<RmsUser> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<RmsUser> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RmsUserSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, RmsUser.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<RmsUser> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<RmsUser> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private RmsUser process(Row row, RowMetadata metadata) {
        RmsUser entity = rmsuserMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends RmsUser> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
