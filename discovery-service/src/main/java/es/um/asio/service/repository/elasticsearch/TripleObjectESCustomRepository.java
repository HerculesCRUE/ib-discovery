package es.um.asio.service.repository.elasticsearch;

import es.um.asio.service.model.elasticsearch.TripleObjectES;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Repository
public class TripleObjectESCustomRepository{

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public List<TripleObjectES> findByClassNameAndAttributesWithPartialMatch(String indexName, String className, List<Pair<String,Object>> attrs){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("className",className));
        BoolQueryBuilder boolQueryBuilderAttrs = QueryBuilders.boolQuery();
        if (attrs.size()>0) {
            for (Pair<String,Object> att : attrs) {
                if (att.getValue(1) instanceof String) {
                    boolQueryBuilderAttrs = boolQueryBuilderAttrs.should(QueryBuilders.matchQuery(String.format("attributes.%s", att.getValue(0)), att.getValue(1)));
                } else {
                    boolQueryBuilderAttrs = boolQueryBuilderAttrs.should(QueryBuilders.termQuery(String.format("attributes.%s", att.getValue(0)), att.getValue(1)));
                }
            }
            boolQueryBuilder.must(boolQueryBuilderAttrs);
        }
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withIndices(indexName)
                .build();

        Set<TripleObjectES> results = new HashSet<>();
        ScrolledPage<TripleObjectES> scroll = (ScrolledPage<TripleObjectES>) elasticsearchTemplate.startScroll(6000, build,TripleObjectES.class);
        while (scroll.hasContent()) {
            results.addAll(scroll.getContent());
            scroll = (ScrolledPage<TripleObjectES>) elasticsearchTemplate.continueScroll(scroll.getScrollId(),6000,TripleObjectES.class);
        }
        elasticsearchTemplate.clearScroll(scroll.getScrollId());
        return new ArrayList<>(results);
    }

    public List<TripleObjectES> getAllTripleObjectsESByClassByClassName(String className) {
        Set<TripleObjectES> results = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("className",className));
        NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        build.addIndices("triple-object");
        build.addTypes("classes");
        build.setPageable(PageRequest.of(0,5000));

        ScrolledPage<TripleObjectES> scroll = (ScrolledPage<TripleObjectES>) elasticsearchTemplate.startScroll(6000, build,TripleObjectES.class);
        while (scroll.hasContent()) {
            results.addAll(scroll.getContent());
            scroll = (ScrolledPage<TripleObjectES>) elasticsearchTemplate.continueScroll(scroll.getScrollId(),6000,TripleObjectES.class);
        }
        elasticsearchTemplate.clearScroll(scroll.getScrollId());

        return new ArrayList<>(results);
    }

    public List<TripleObjectES> getAllTripleObjectsES() {
        Set<TripleObjectES> results = new HashSet<>();
        NativeSearchQuery build = new NativeSearchQueryBuilder().build();
        build.addIndices("triple-object");
        build.addTypes("classes");
        build.setPageable(PageRequest.of(0,5000));

        ScrolledPage<TripleObjectES> scroll = (ScrolledPage<TripleObjectES>) elasticsearchTemplate.startScroll(6000, build,TripleObjectES.class);
        while (scroll.hasContent()) {
            results.addAll(scroll.getContent());
            scroll = (ScrolledPage<TripleObjectES>) elasticsearchTemplate.continueScroll(scroll.getScrollId(),6000,TripleObjectES.class);
        }
        elasticsearchTemplate.clearScroll(scroll.getScrollId());

        return new ArrayList<>(results);
    }
}
