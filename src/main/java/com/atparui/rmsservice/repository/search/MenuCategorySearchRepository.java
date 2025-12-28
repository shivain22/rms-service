package com.atparui.rmsservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.atparui.rmsservice.domain.MenuCategory;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MenuCategory} entity.
 */
public interface MenuCategorySearchRepository
    extends ReactiveElasticsearchRepository<MenuCategory, UUID>, MenuCategorySearchRepositoryInternal {}

interface MenuCategorySearchRepositoryInternal {
    Flux<MenuCategory> search(String query, Pageable pageable);

    Flux<MenuCategory> search(Query query);
}

class MenuCategorySearchRepositoryInternalImpl implements MenuCategorySearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MenuCategorySearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MenuCategory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MenuCategory> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MenuCategory.class).map(SearchHit::getContent);
    }
}
