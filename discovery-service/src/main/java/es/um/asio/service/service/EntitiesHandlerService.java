package es.um.asio.service.service;

import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;

import java.util.Date;
import java.util.Set;

/**
 * EntitiesHandlerService interface. For handle the search of similarities
 * @see SimilarityResult
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface EntitiesHandlerService {

    /**
     * Handle the search of the similarities between entities, filtered by Node Triple Store and Class Name
     * @param node String. The Node name to filter
     * @param tripleStore. The Triple Store name to filter
     * @param className. The Class Name to filter
     * @param searchInOtherNodes boolean. If true, then a search will be made in other nodes to find links, in another case only look for similarities in the same node
     * @param deltaDate boolean. If true, then Similarities will only be searched for entities that have changed since the last search, otherwise it will be performed on all entities
     * @return Set<SimilarityResult>, with the Similarities results, in the form SimilarityResult
     */
    Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClass (String node, String tripleStore, String className, boolean searchInOtherNodes, Date deltaDate);

    /**
     * Handle the search of the similarities between the entity pass as parameter and the rest of entities
     * @param tripleObject TripleObject. The Object for search similarities
     * @param searchInOtherNodes boolean. If true, then a search will be made in other nodes to find links, in another case only look for similarities in the same node
     * @return SimilarityResult, with the Similarities results, in the form SimilarityResult
     */
    SimilarityResult findEntitiesLinksByNodeAndTripleStoreAndTripleObject (TripleObject tripleObject, boolean searchInOtherNodes);

    /**
     * Handle the search of the similarities in the LOD Cloud with the datasets defined
     * @link "https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/24-Librer%C3%ADa_de_descubrimiento/ASIO_Libreria_de_descubrimiento.md#descubrimiento-de-enlaces-entre-entidades-en-la-nube-lod"
     * @param dataSource String. The name of the dataset to search
     * @param node String. The Node name to filter
     * @param tripleStore. The Triple Store name to filter
     * @param className. The Class Name to filter
     * @param deltaDate boolean. If true, then Similarities will only be searched for entities that have changed since the last search, otherwise it will be performed on all entities
     * @return Set<SimilarityResult>
     */
    Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClassInLOD (String dataSource,String node, String tripleStore, String className, Date deltaDate);
}
