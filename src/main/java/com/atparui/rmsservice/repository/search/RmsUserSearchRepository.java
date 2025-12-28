package com.atparui.rmsservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.atparui.rmsservice.domain.RmsUser;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link RmsUser} entity.
 */
public interface RmsUserSearchRepository extends ReactiveElasticsearchRepository<RmsUser, UUID>, RmsUserSearchRepositoryInternal {}

interface RmsUserSearchRepositoryInternal {
    Flux<RmsUser> search(String query, Pageable pageable);

    Flux<RmsUser> search(Query query);
}

class RmsUserSearchRepositoryInternalImpl implements RmsUserSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    RmsUserSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<RmsUser> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<RmsUser> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, RmsUser.class).map(SearchHit::getContent);
    }
}
