package com.atparui.rmsservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.atparui.rmsservice.domain.Bill;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Bill} entity.
 */
public interface BillSearchRepository extends ReactiveElasticsearchRepository<Bill, UUID>, BillSearchRepositoryInternal {}

interface BillSearchRepositoryInternal {
    Flux<Bill> search(String query, Pageable pageable);

    Flux<Bill> search(Query query);
}

class BillSearchRepositoryInternalImpl implements BillSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    BillSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Bill> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Bill> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Bill.class).map(SearchHit::getContent);
    }
}
