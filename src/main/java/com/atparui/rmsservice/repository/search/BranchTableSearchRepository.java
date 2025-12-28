package com.atparui.rmsservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.atparui.rmsservice.domain.BranchTable;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link BranchTable} entity.
 */
public interface BranchTableSearchRepository
    extends ReactiveElasticsearchRepository<BranchTable, UUID>, BranchTableSearchRepositoryInternal {}

interface BranchTableSearchRepositoryInternal {
    Flux<BranchTable> search(String query, Pageable pageable);

    Flux<BranchTable> search(Query query);
}

class BranchTableSearchRepositoryInternalImpl implements BranchTableSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    BranchTableSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<BranchTable> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<BranchTable> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, BranchTable.class).map(SearchHit::getContent);
    }
}
