package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.TableWaiterAssignment;
import com.atparui.rmsservice.repository.rowmapper.RmsUserRowMapper;
import com.atparui.rmsservice.repository.rowmapper.TableAssignmentRowMapper;
import com.atparui.rmsservice.repository.rowmapper.TableWaiterAssignmentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TableWaiterAssignment entity.
 */
@SuppressWarnings("unused")
class TableWaiterAssignmentRepositoryInternalImpl
    extends SimpleR2dbcRepository<TableWaiterAssignment, UUID>
    implements TableWaiterAssignmentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TableAssignmentRowMapper tableassignmentMapper;
    private final RmsUserRowMapper rmsuserMapper;
    private final TableWaiterAssignmentRowMapper tablewaiterassignmentMapper;

    private static final Table entityTable = Table.aliased("table_waiter_assignment", EntityManager.ENTITY_ALIAS);
    private static final Table tableAssignmentTable = Table.aliased("table_assignment", "tableAssignment");
    private static final Table waiterTable = Table.aliased("rms_user", "waiter");

    public TableWaiterAssignmentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TableAssignmentRowMapper tableassignmentMapper,
        RmsUserRowMapper rmsuserMapper,
        TableWaiterAssignmentRowMapper tablewaiterassignmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TableWaiterAssignment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.tableassignmentMapper = tableassignmentMapper;
        this.rmsuserMapper = rmsuserMapper;
        this.tablewaiterassignmentMapper = tablewaiterassignmentMapper;
    }

    @Override
    public Flux<TableWaiterAssignment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TableWaiterAssignment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TableWaiterAssignmentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TableAssignmentSqlHelper.getColumns(tableAssignmentTable, "tableAssignment"));
        columns.addAll(RmsUserSqlHelper.getColumns(waiterTable, "waiter"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(tableAssignmentTable)
            .on(Column.create("table_assignment_id", entityTable))
            .equals(Column.create("id", tableAssignmentTable))
            .leftOuterJoin(waiterTable)
            .on(Column.create("waiter_id", entityTable))
            .equals(Column.create("id", waiterTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TableWaiterAssignment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TableWaiterAssignment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TableWaiterAssignment> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private TableWaiterAssignment process(Row row, RowMetadata metadata) {
        TableWaiterAssignment entity = tablewaiterassignmentMapper.apply(row, "e");
        entity.setTableAssignment(tableassignmentMapper.apply(row, "tableAssignment"));
        entity.setWaiter(rmsuserMapper.apply(row, "waiter"));
        return entity;
    }

    @Override
    public <S extends TableWaiterAssignment> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
