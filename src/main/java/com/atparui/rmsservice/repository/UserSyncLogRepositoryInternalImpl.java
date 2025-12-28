package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.UserSyncLog;
import com.atparui.rmsservice.repository.rowmapper.RmsUserRowMapper;
import com.atparui.rmsservice.repository.rowmapper.UserSyncLogRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the UserSyncLog entity.
 */
@SuppressWarnings("unused")
class UserSyncLogRepositoryInternalImpl extends SimpleR2dbcRepository<UserSyncLog, UUID> implements UserSyncLogRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RmsUserRowMapper rmsuserMapper;
    private final UserSyncLogRowMapper usersynclogMapper;

    private static final Table entityTable = Table.aliased("user_sync_log", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("rms_user", "e_user");

    public UserSyncLogRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RmsUserRowMapper rmsuserMapper,
        UserSyncLogRowMapper usersynclogMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(UserSyncLog.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rmsuserMapper = rmsuserMapper;
        this.usersynclogMapper = usersynclogMapper;
    }

    @Override
    public Flux<UserSyncLog> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<UserSyncLog> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = UserSyncLogSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RmsUserSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, UserSyncLog.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<UserSyncLog> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<UserSyncLog> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private UserSyncLog process(Row row, RowMetadata metadata) {
        UserSyncLog entity = usersynclogMapper.apply(row, "e");
        entity.setUser(rmsuserMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends UserSyncLog> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
