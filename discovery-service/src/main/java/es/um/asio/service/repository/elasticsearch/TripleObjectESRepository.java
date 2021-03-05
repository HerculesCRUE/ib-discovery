package es.um.asio.service.repository.elasticsearch;

import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.service.impl.TextHandlerServiceImp;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * TripleObjectESCustomRepository Interface. Create and handle queries in elasticsearch.
 * @see ElasticsearchTemplate
 * @see TextHandlerServiceImp
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface TripleObjectESRepository extends ElasticsearchRepository<TripleObjectES,String> {

    /**
     * Get all Class and Ids stored in elasticsearch
     * @see TripleObjectES
     * @param node String. The node name
     * @param tripleStore String. The triple store name
     * @return Map<String,Set<String>>. All Class and Ids stored in elasticsearch
     */
    List<TripleObjectES> findByEntityIdAndClassName(String id, String className);

}
