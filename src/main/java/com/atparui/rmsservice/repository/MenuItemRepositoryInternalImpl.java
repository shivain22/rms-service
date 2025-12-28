package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.MenuItem;
import com.atparui.rmsservice.repository.rowmapper.BranchRowMapper;
import com.atparui.rmsservice.repository.rowmapper.MenuCategoryRowMapper;
import com.atparui.rmsservice.repository.rowmapper.MenuItemRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MenuItem entity.
 */
@SuppressWarnings("unused")
class MenuItemRepositoryInternalImpl extends SimpleR2dbcRepository<MenuItem, UUID> implements MenuItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BranchRowMapper branchMapper;
    private final MenuCategoryRowMapper menucategoryMapper;
    private final MenuItemRowMapper menuitemMapper;

    private static final Table entityTable = Table.aliased("menu_item", EntityManager.ENTITY_ALIAS);
    private static final Table branchTable = Table.aliased("branch", "branch");
    private static final Table menuCategoryTable = Table.aliased("menu_category", "menuCategory");

    public MenuItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BranchRowMapper branchMapper,
        MenuCategoryRowMapper menucategoryMapper,
        MenuItemRowMapper menuitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MenuItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.branchMapper = branchMapper;
        this.menucategoryMapper = menucategoryMapper;
        this.menuitemMapper = menuitemMapper;
    }

    @Override
    public Flux<MenuItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MenuItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MenuItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BranchSqlHelper.getColumns(branchTable, "branch"));
        columns.addAll(MenuCategorySqlHelper.getColumns(menuCategoryTable, "menuCategory"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(branchTable)
            .on(Column.create("branch_id", entityTable))
            .equals(Column.create("id", branchTable))
            .leftOuterJoin(menuCategoryTable)
            .on(Column.create("menu_category_id", entityTable))
            .equals(Column.create("id", menuCategoryTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MenuItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MenuItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MenuItem> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private MenuItem process(Row row, RowMetadata metadata) {
        MenuItem entity = menuitemMapper.apply(row, "e");
        entity.setBranch(branchMapper.apply(row, "branch"));
        entity.setMenuCategory(menucategoryMapper.apply(row, "menuCategory"));
        return entity;
    }

    @Override
    public <S extends MenuItem> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
