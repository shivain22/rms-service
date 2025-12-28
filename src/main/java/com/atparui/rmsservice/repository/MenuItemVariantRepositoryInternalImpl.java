package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.MenuItemVariant;
import com.atparui.rmsservice.repository.rowmapper.MenuItemRowMapper;
import com.atparui.rmsservice.repository.rowmapper.MenuItemVariantRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MenuItemVariant entity.
 */
@SuppressWarnings("unused")
class MenuItemVariantRepositoryInternalImpl
    extends SimpleR2dbcRepository<MenuItemVariant, UUID>
    implements MenuItemVariantRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MenuItemRowMapper menuitemMapper;
    private final MenuItemVariantRowMapper menuitemvariantMapper;

    private static final Table entityTable = Table.aliased("menu_item_variant", EntityManager.ENTITY_ALIAS);
    private static final Table menuItemTable = Table.aliased("menu_item", "menuItem");

    public MenuItemVariantRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MenuItemRowMapper menuitemMapper,
        MenuItemVariantRowMapper menuitemvariantMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MenuItemVariant.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.menuitemMapper = menuitemMapper;
        this.menuitemvariantMapper = menuitemvariantMapper;
    }

    @Override
    public Flux<MenuItemVariant> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MenuItemVariant> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MenuItemVariantSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MenuItemSqlHelper.getColumns(menuItemTable, "menuItem"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(menuItemTable)
            .on(Column.create("menu_item_id", entityTable))
            .equals(Column.create("id", menuItemTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MenuItemVariant.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MenuItemVariant> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MenuItemVariant> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private MenuItemVariant process(Row row, RowMetadata metadata) {
        MenuItemVariant entity = menuitemvariantMapper.apply(row, "e");
        entity.setMenuItem(menuitemMapper.apply(row, "menuItem"));
        return entity;
    }

    @Override
    public <S extends MenuItemVariant> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
