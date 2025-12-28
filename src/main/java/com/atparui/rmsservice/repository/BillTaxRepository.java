package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.BillTax;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the BillTax entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillTaxRepository extends ReactiveCrudRepository<BillTax, UUID>, BillTaxRepositoryInternal {
    @Query("SELECT * FROM bill_tax entity WHERE entity.bill_id = :id")
    Flux<BillTax> findByBill(UUID id);

    @Query("SELECT * FROM bill_tax entity WHERE entity.bill_id IS NULL")
    Flux<BillTax> findAllWhereBillIsNull();

    @Query("SELECT * FROM bill_tax entity WHERE entity.tax_config_id = :id")
    Flux<BillTax> findByTaxConfig(UUID id);

    @Query("SELECT * FROM bill_tax entity WHERE entity.tax_config_id IS NULL")
    Flux<BillTax> findAllWhereTaxConfigIsNull();

    @Override
    <S extends BillTax> Mono<S> save(S entity);

    @Override
    Flux<BillTax> findAll();

    @Override
    Mono<BillTax> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface BillTaxRepositoryInternal {
    <S extends BillTax> Mono<S> save(S entity);

    Flux<BillTax> findAllBy(Pageable pageable);

    Flux<BillTax> findAll();

    Mono<BillTax> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<BillTax> findAllBy(Pageable pageable, Criteria criteria);
}
