package com.atparui.rmsservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.atparui.rmsservice.domain.MenuItem;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MenuItem} entity.
 */
public interface MenuItemSearchRepository extends ReactiveElasticsearchRepository<MenuItem, UUID>, MenuItemSearchRepositoryInternal {}

interface MenuItemSearchRepositoryInternal {
    Flux<MenuItem> search(String query, Pageable pageable);

    Flux<MenuItem> search(Query query);
}

class MenuItemSearchRepositoryInternalImpl implements MenuItemSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MenuItemSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MenuItem> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MenuItem> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MenuItem.class).map(SearchHit::getContent);
    }
}
