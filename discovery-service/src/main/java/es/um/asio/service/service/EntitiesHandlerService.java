package es.um.asio.service.service;

import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;

import java.util.Date;
import java.util.Set;

public interface EntitiesHandlerService {

    Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClass (String node, String tripleStore, String className, boolean searchInOtherNodes, Date deltaDate);

    SimilarityResult findEntitiesLinksByNodeAndTripleStoreAndTripleObject (TripleObject tripleObject, boolean searchInOtherNodes);

    Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClassInLOD (String dataSource,String node, String tripleStore, String className, Date deltaDate);
}
