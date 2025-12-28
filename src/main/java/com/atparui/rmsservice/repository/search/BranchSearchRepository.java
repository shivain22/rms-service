package com.atparui.rmsservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.atparui.rmsservice.domain.Branch;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Branch} entity.
 */
public interface BranchSearchRepository extends ReactiveElasticsearchRepository<Branch, UUID>, BranchSearchRepositoryInternal {}

interface BranchSearchRepositoryInternal {
    Flux<Branch> search(String query, Pageable pageable);

    Flux<Branch> search(Query query);
}

class BranchSearchRepositoryInternalImpl implements BranchSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    BranchSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Branch> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Branch> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Branch.class).map(SearchHit::getContent);
    }
}
