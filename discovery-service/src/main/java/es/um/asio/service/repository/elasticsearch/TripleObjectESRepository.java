package es.um.asio.service.repository.elasticsearch;


import es.um.asio.service.model.elasticsearch.TripleObjectES;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TripleObjectESRepository extends ElasticsearchRepository<TripleObjectES,String> {

    Page<TripleObjectES> findById(String id, Pageable pageable);
}
