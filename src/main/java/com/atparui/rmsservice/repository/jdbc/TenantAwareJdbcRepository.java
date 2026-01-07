package com.atparui.rmsservice.repository.jdbc;

import com.atparui.rmsservice.tenant.TenantConnectionProvider;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Base implementation for tenant-aware JDBC repositories.
 * Automatically uses the correct DataSource based on tenant context.
 *
 * @param <T> the entity type
 * @param <ID> the ID type
 */
public abstract class TenantAwareJdbcRepository<T, ID> implements JdbcRepository<T, ID> {

    private static final Logger LOG = LoggerFactory.getLogger(TenantAwareJdbcRepository.class);

    protected final TenantConnectionProvider connectionProvider;
    protected JdbcTemplate jdbcTemplate;

    public TenantAwareJdbcRepository(TenantConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Get the JdbcTemplate for the current tenant.
     * Creates a new JdbcTemplate if the tenant has changed.
     *
     * @return JdbcTemplate for the current tenant
     */
    protected JdbcTemplate getJdbcTemplate() {
        DataSource dataSource = connectionProvider.getJdbcDataSource();
        if (dataSource == null) {
            throw new IllegalStateException(
                "Current tenant does not use JDBC. Check tenant configuration or use R2DBC repository instead."
            );
        }
        // Create new JdbcTemplate each time to ensure we use the correct DataSource
        // (DataSource may change if tenant changes)
        return new JdbcTemplate(dataSource);
    }

    /**
     * Get the RowMapper for this entity type.
     *
     * @return RowMapper instance
     */
    protected abstract RowMapper<T> getRowMapper();

    /**
     * Get the table name for this entity.
     *
     * @return table name
     */
    protected abstract String getTableName();

    /**
     * Get the ID column name.
     *
     * @return ID column name
     */
    protected abstract String getIdColumnName();

    /**
     * Map entity to parameters for INSERT statement.
     *
     * @param entity the entity
     * @return array of parameters
     */
    protected abstract Object[] getInsertParameters(T entity);

    /**
     * Get the INSERT SQL statement.
     *
     * @return INSERT SQL
     */
    protected abstract String getInsertSql();

    /**
     * Get the UPDATE SQL statement.
     *
     * @return UPDATE SQL
     */
    protected abstract String getUpdateSql();

    @Override
    public T save(T entity) {
        JdbcTemplate template = getJdbcTemplate();
        ID id = getId(entity);
        if (id == null || !existsById(id)) {
            // Insert
            template.update(getInsertSql(), getInsertParameters(entity));
            return entity;
        } else {
            // Update
            Object[] updateParams = getUpdateParameters(entity);
            template.update(getUpdateSql(), updateParams);
            return entity;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        JdbcTemplate template = getJdbcTemplate();
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        try {
            T entity = template.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(entity);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        JdbcTemplate template = getJdbcTemplate();
        String sql = "SELECT * FROM " + getTableName();
        return template.query(sql, getRowMapper());
    }

    @Override
    public void deleteById(ID id) {
        JdbcTemplate template = getJdbcTemplate();
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        template.update(sql, id);
    }

    @Override
    public boolean existsById(ID id) {
        JdbcTemplate template = getJdbcTemplate();
        String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        Integer count = template.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public long count() {
        JdbcTemplate template = getJdbcTemplate();
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        Long count = template.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    /**
     * Extract ID from entity.
     *
     * @param entity the entity
     * @return the ID
     */
    protected abstract ID getId(T entity);

    /**
     * Map entity to parameters for UPDATE statement.
     *
     * @param entity the entity
     * @return array of parameters
     */
    protected abstract Object[] getUpdateParameters(T entity);
}
