package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.AppNavigationMenuRole;
import com.atparui.rmsservice.repository.rowmapper.AppNavigationMenuItemRowMapper;
import com.atparui.rmsservice.repository.rowmapper.AppNavigationMenuRoleRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the AppNavigationMenuRole entity.
 */
@SuppressWarnings("unused")
class AppNavigationMenuRoleRepositoryInternalImpl
    extends SimpleR2dbcRepository<AppNavigationMenuRole, UUID>
    implements AppNavigationMenuRoleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AppNavigationMenuRowMapper appnavigationmenuMapper;
    private final AppNavigationMenuItemRowMapper appnavigationmenuitemMapper;
    private final AppNavigationMenuRoleRowMapper appnavigationmenuroleMapper;

    private static final Table entityTable = Table.aliased("app_navigation_menu_role", EntityManager.ENTITY_ALIAS);
    private static final Table appNavigationMenuTable = Table.aliased("app_navigation_menu", "appNavigationMenu");
    private static final Table appNavigationMenuItemTable = Table.aliased("app_navigation_menu_item", "appNavigationMenuItem");

    public AppNavigationMenuRoleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AppNavigationMenuRowMapper appnavigationmenuMapper,
        AppNavigationMenuItemRowMapper appnavigationmenuitemMapper,
        AppNavigationMenuRoleRowMapper appnavigationmenuroleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AppNavigationMenuRole.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.appnavigationmenuMapper = appnavigationmenuMapper;
        this.appnavigationmenuitemMapper = appnavigationmenuitemMapper;
        this.appnavigationmenuroleMapper = appnavigationmenuroleMapper;
    }

    @Override
    public Flux<AppNavigationMenuRole> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AppNavigationMenuRole> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AppNavigationMenuRoleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AppNavigationMenuSqlHelper.getColumns(appNavigationMenuTable, "appNavigationMenu"));
        columns.addAll(AppNavigationMenuItemSqlHelper.getColumns(appNavigationMenuItemTable, "appNavigationMenuItem"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(appNavigationMenuTable)
            .on(Column.create("app_navigation_menu_id", entityTable))
            .equals(Column.create("id", appNavigationMenuTable))
            .leftOuterJoin(appNavigationMenuItemTable)
            .on(Column.create("app_navigation_menu_item_id", entityTable))
            .equals(Column.create("id", appNavigationMenuItemTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AppNavigationMenuRole.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AppNavigationMenuRole> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AppNavigationMenuRole> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private AppNavigationMenuRole process(Row row, RowMetadata metadata) {
        AppNavigationMenuRole entity = appnavigationmenuroleMapper.apply(row, "e");
        entity.setAppNavigationMenu(appnavigationmenuMapper.apply(row, "appNavigationMenu"));
        entity.setAppNavigationMenuItem(appnavigationmenuitemMapper.apply(row, "appNavigationMenuItem"));
        return entity;
    }

    @Override
    public <S extends AppNavigationMenuRole> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
