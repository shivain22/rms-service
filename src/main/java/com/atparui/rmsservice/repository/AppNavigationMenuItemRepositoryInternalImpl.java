package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.AppNavigationMenuItem;
import com.atparui.rmsservice.repository.rowmapper.AppNavigationMenuItemRowMapper;
import com.atparui.rmsservice.repository.rowmapper.AppNavigationMenuRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the AppNavigationMenuItem entity.
 */
@SuppressWarnings("unused")
class AppNavigationMenuItemRepositoryInternalImpl
    extends SimpleR2dbcRepository<AppNavigationMenuItem, UUID>
    implements AppNavigationMenuItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AppNavigationMenuRowMapper appnavigationmenuMapper;
    private final AppNavigationMenuItemRowMapper appnavigationmenuitemMapper;

    private static final Table entityTable = Table.aliased("app_navigation_menu_item", EntityManager.ENTITY_ALIAS);
    private static final Table parentMenuTable = Table.aliased("app_navigation_menu", "parentMenu");

    public AppNavigationMenuItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AppNavigationMenuRowMapper appnavigationmenuMapper,
        AppNavigationMenuItemRowMapper appnavigationmenuitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AppNavigationMenuItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.appnavigationmenuMapper = appnavigationmenuMapper;
        this.appnavigationmenuitemMapper = appnavigationmenuitemMapper;
    }

    @Override
    public Flux<AppNavigationMenuItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AppNavigationMenuItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AppNavigationMenuItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AppNavigationMenuSqlHelper.getColumns(parentMenuTable, "parentMenu"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parentMenuTable)
            .on(Column.create("parent_menu_id", entityTable))
            .equals(Column.create("id", parentMenuTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AppNavigationMenuItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AppNavigationMenuItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AppNavigationMenuItem> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private AppNavigationMenuItem process(Row row, RowMetadata metadata) {
        AppNavigationMenuItem entity = appnavigationmenuitemMapper.apply(row, "e");
        entity.setParentMenu(appnavigationmenuMapper.apply(row, "parentMenu"));
        return entity;
    }

    @Override
    public <S extends AppNavigationMenuItem> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
