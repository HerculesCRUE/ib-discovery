package es.um.asio.service.repository.elasticsearch;

import es.um.asio.service.model.elasticsearch.TripleObjectES;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TripleObjectESRepository extends ElasticsearchRepository<TripleObjectES,String> {

    List<TripleObjectES> findByIdAndClassName(String id, String className);

/*    @Query("{\"bool\": " +
            "{\"must\": [" +
                "{\"query\": \"?0\"}}}"
    )
    List<TripleObjectES> findByClassNameAndAtt(String className);*/
}