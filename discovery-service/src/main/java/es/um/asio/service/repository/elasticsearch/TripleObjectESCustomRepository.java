package es.um.asio.service.repository.elasticsearch;

import es.um.asio.service.model.elasticsearch.TripleObjectES;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TripleObjectESCustomRepository{

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public List<Object> findByClassNameAndAttributesWithPartialMatch(Class c, String indexName, String className, List<Pair<String,Object>> attrs){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery("className","clase1"));
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

        return elasticsearchTemplate.queryForList(build,c);
    }
}
