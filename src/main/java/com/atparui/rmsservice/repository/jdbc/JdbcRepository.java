package com.atparui.rmsservice.repository.jdbc;

import java.util.List;
import java.util.Optional;

/**
 * Base interface for JDBC-based repositories.
 * Provides basic CRUD operations using JDBC.
 *
 * @param <T> the entity type
 * @param <ID> the ID type
 */
public interface JdbcRepository<T, ID> {
    /**
     * Save an entity.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);

    /**
     * Find an entity by ID.
     *
     * @param id the entity ID
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Find all entities.
     *
     * @return list of all entities
     */
    List<T> findAll();

    /**
     * Delete an entity by ID.
     *
     * @param id the entity ID
     */
    void deleteById(ID id);

    /**
     * Check if an entity exists by ID.
     *
     * @param id the entity ID
     * @return true if entity exists
     */
    boolean existsById(ID id);

    /**
     * Count all entities.
     *
     * @return the count
     */
    long count();
}
