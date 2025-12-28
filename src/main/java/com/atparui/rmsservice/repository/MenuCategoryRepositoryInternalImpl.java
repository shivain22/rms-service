package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.MenuCategory;
import com.atparui.rmsservice.repository.rowmapper.MenuCategoryRowMapper;
import com.atparui.rmsservice.repository.rowmapper.RestaurantRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MenuCategory entity.
 */
@SuppressWarnings("unused")
class MenuCategoryRepositoryInternalImpl extends SimpleR2dbcRepository<MenuCategory, UUID> implements MenuCategoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RestaurantRowMapper restaurantMapper;
    private final MenuCategoryRowMapper menucategoryMapper;

    private static final Table entityTable = Table.aliased("menu_category", EntityManager.ENTITY_ALIAS);
    private static final Table restaurantTable = Table.aliased("restaurant", "restaurant");

    public MenuCategoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RestaurantRowMapper restaurantMapper,
        MenuCategoryRowMapper menucategoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MenuCategory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.restaurantMapper = restaurantMapper;
        this.menucategoryMapper = menucategoryMapper;
    }

    @Override
    public Flux<MenuCategory> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MenuCategory> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MenuCategorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RestaurantSqlHelper.getColumns(restaurantTable, "restaurant"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(restaurantTable)
            .on(Column.create("restaurant_id", entityTable))
            .equals(Column.create("id", restaurantTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MenuCategory.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MenuCategory> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MenuCategory> findById(UUID id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    private MenuCategory process(Row row, RowMetadata metadata) {
        MenuCategory entity = menucategoryMapper.apply(row, "e");
        entity.setRestaurant(restaurantMapper.apply(row, "restaurant"));
        return entity;
    }

    @Override
    public <S extends MenuCategory> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
