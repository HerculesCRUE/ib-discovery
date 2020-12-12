package es.um.asio.service.repository.elasticsearch;

import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.service.impl.TextHandlerServiceImp;
import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TripleObjectESCustomRepository{

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private TextHandlerServiceImp textHandler;

    private static final String TRIPLE_OBJECT = "triple-object";
    private static final String CLASSES = "classes";

    public List<TripleObjectES> findByClassNameAndAttributesWithPartialMatch(String indexName, List<Triplet<String,String,String>> musts, List<Pair<String,Object>> attrs){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Triplet<String,String,String> must : musts) {
            if (must.getValue2().equals("TERM"))
                boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery(must.getValue0(), must.getValue1()));
            else
                boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.matchQuery(must.getValue0(), must.getValue1()));
        }
        BoolQueryBuilder boolQueryBuilderAttrs = QueryBuilders.boolQuery();
        if (!attrs.isEmpty()) {
            for (Pair<String,Object> att : attrs) {
                if (att.getValue(1) instanceof String) {
                    boolQueryBuilderAttrs = boolQueryBuilderAttrs.should(QueryBuilders.matchQuery(String.format("attributes.%s", att.getValue(0)), textHandler.removeStopWords((String) att.getValue(1))));
                } else {
                    if (att.getValue(1) instanceof List) {
                        for (Object val : (List)att.getValue(1)) {
                            if (val instanceof String) {
                                if (!Utils.isDate((String) val))
                                    boolQueryBuilderAttrs = boolQueryBuilderAttrs.should(QueryBuilders.matchQuery(String.format("attributes.%s", att.getValue(0)), textHandler.removeStopWords((String) val)));
                                else
                                    boolQueryBuilderAttrs = boolQueryBuilderAttrs.should(QueryBuilders.matchQuery(String.format("attributes.%s", att.getValue(0)), String.valueOf(val)));
                            } else {
                                boolQueryBuilderAttrs = boolQueryBuilderAttrs.should(QueryBuilders.termQuery(String.format("attributes.%s", att.getValue(0)), val));
                            }
                        }
                    } else {
                        boolQueryBuilderAttrs = boolQueryBuilderAttrs.should(QueryBuilders.termQuery(String.format("attributes.%s", att.getValue(0)), att.getValue(1)));
                    }
                }
            }
            boolQueryBuilder.must(boolQueryBuilderAttrs);
        }
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withIndices(indexName)
                .build();

        Set<TripleObjectES> results = new HashSet<>();
        ScrolledPage<TripleObjectES> scroll = elasticsearchTemplate.startScroll(6000, build,TripleObjectES.class);
        while (scroll.hasContent()) {
            results.addAll(scroll.getContent());
            scroll = elasticsearchTemplate.continueScroll(scroll.getScrollId(),6000,TripleObjectES.class);
        }
        elasticsearchTemplate.clearScroll(scroll.getScrollId());
        List<TripleObjectES> lResult = new ArrayList<>(results);
        Collections.sort(lResult);
        return lResult;
    }

    public List<TripleObjectES> getAllTripleObjectsESByNodeAndTripleStoreAndClassName(String node,String tripleStore,String className) {
        Set<TripleObjectES> results = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("tripleStore.node.node", node));
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("tripleStore.tripleStore", tripleStore));
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("className", className));
        NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        build.addIndices(TRIPLE_OBJECT);
        build.addTypes(CLASSES);
        build.setPageable(PageRequest.of(0,5000));

        ScrolledPage<TripleObjectES> scroll =  elasticsearchTemplate.startScroll(6000, build,TripleObjectES.class);
        while (scroll.hasContent()) {
            results.addAll(scroll.getContent());
            scroll = elasticsearchTemplate.continueScroll(scroll.getScrollId(),6000,TripleObjectES.class);
        }
        elasticsearchTemplate.clearScroll(scroll.getScrollId());

        return new ArrayList<>(results);
    }

    public List<TripleObjectES> getAllTripleObjectsES() {
        Set<TripleObjectES> results = new HashSet<>();
        NativeSearchQuery build = new NativeSearchQueryBuilder().build();
        build.addIndices(TRIPLE_OBJECT);
        build.addTypes(CLASSES);
        build.setPageable(PageRequest.of(0,5000));

        ScrolledPage<TripleObjectES> scroll = elasticsearchTemplate.startScroll(6000, build,TripleObjectES.class);
        while (scroll.hasContent()) {
            results.addAll(scroll.getContent());
            scroll = elasticsearchTemplate.continueScroll(scroll.getScrollId(),6000,TripleObjectES.class);
        }
        elasticsearchTemplate.clearScroll(scroll.getScrollId());

        return new ArrayList<>(results);
    }

    public Map<String,Set<String>>getAllClassAndId(String node, String tripleStore) {
        Map<String,Set<String>> results = new HashMap<>();

        String[] includes = new String[]{"entityId", "className"};

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("tripleStore.node.node", node));
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("tripleStore.tripleStore", tripleStore));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSourceFilter(
                        new FetchSourceFilterBuilder()
                                .withIncludes(includes)
                                .build()
                ).build();

        searchQuery.addIndices(TRIPLE_OBJECT);
        searchQuery.addTypes(CLASSES);
        searchQuery.setPageable(PageRequest.of(0,5000));

        ScrolledPage<PairES> scroll = elasticsearchTemplate.startScroll(6000, searchQuery,PairES.class);
        while (scroll.hasContent()) {
            List<PairES> pairsES = scroll.getContent();
            for (PairES p : pairsES) {
                if (!results.containsKey(p.getClassName())) {
                    results.put(p.getClassName(), new HashSet<>());
                }
                results.get(p.getClassName()).add(p.getEntityId());
            }
            scroll = elasticsearchTemplate.continueScroll(scroll.getScrollId(),6000,PairES.class);
        }
        elasticsearchTemplate.clearScroll(scroll.getScrollId());

        return results;
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class PairES {
    private String entityId;
    private String className;
}
