package com.atparui.rmsservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.atparui.rmsservice.domain.Restaurant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Restaurant} entity.
 */
public interface RestaurantSearchRepository extends ReactiveElasticsearchRepository<Restaurant, UUID>, RestaurantSearchRepositoryInternal {}

interface RestaurantSearchRepositoryInternal {
    Flux<Restaurant> search(String query, Pageable pageable);

    Flux<Restaurant> search(Query query);
}

class RestaurantSearchRepositoryInternalImpl implements RestaurantSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    RestaurantSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Restaurant> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Restaurant> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Restaurant.class).map(SearchHit::getContent);
    }
}
