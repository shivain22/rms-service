package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.TableAssignment;
import com.atparui.rmsservice.repository.rowmapper.BranchTableRowMapper;
import com.atparui.rmsservice.repository.rowmapper.RmsUserRowMapper;
import com.atparui.rmsservice.repository.rowmapper.ShiftRowMapper;
import com.atparui.rmsservice.repository.rowmapper.TableAssignmentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TableAssignment entity.
 */
@SuppressWarnings("unused")
class TableAssignmentRepositoryInternalImpl
    extends SimpleR2dbcRepository<TableAssignment, UUID>
    implements TableAssignmentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BranchTableRowMapper branchtableMapper;
    private final ShiftRowMapper shiftMapper;
    private final RmsUserRowMapper rmsuserMapper;
    private final TableAssignmentRowMapper tableassignmentMapper;

    private static final Table entityTable = Table.aliased("table_assignment", EntityManager.ENTITY_ALIAS);
    private static final Table branchTableTable = Table.aliased("branch_table", "branchTable");
    private static final Table shiftTable = Table.aliased("shift", "shift");
    private static final Table supervisorTable = Table.aliased("rms_user", "supervisor");

    public TableAssignmentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BranchTableRowMapper branchtableMapper,
        ShiftRowMapper shiftMapper,
        RmsUserRowMapper rmsuserMapper,
        TableAssignmentRowMapper tableassignmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TableAssignment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.branchtableMapper = branchtableMapper;
        this.shiftMapper = shiftMapper;
        this.rmsuserMapper = rmsuserMapper;
        this.tableassignmentMapper = tableassignmentMapper;
    }

    @Override
    public Flux<TableAssignment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TableAssignment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TableAssignmentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BranchTableSqlHelper.getColumns(branchTableTable, "branchTable"));
        columns.addAll(ShiftSqlHelper.getColumns(shiftTable, "shift"));
        columns.addAll(RmsUserSqlHelper.getColumns(supervisorTable, "supervisor"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(branchTableTable)
            .on(Column.create("branch_table_id", entityTable))
            .equals(Column.create("id", branchTableTable))
            .leftOuterJoin(shiftTable)
            .on(Column.create("shift_id", entityTable))
            .equals(Column.create("id", shiftTable))
            .leftOuterJoin(supervisorTable)
            .on(Column.create("supervisor_id", entityTable))
            .equals(Column.create("id", supervisorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TableAssignment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TableAssignment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TableAssignment> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private TableAssignment process(Row row, RowMetadata metadata) {
        TableAssignment entity = tableassignmentMapper.apply(row, "e");
        entity.setBranchTable(branchtableMapper.apply(row, "branchTable"));
        entity.setShift(shiftMapper.apply(row, "shift"));
        entity.setSupervisor(rmsuserMapper.apply(row, "supervisor"));
        return entity;
    }

    @Override
    public <S extends TableAssignment> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
