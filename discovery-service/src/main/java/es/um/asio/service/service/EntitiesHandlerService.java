package es.um.asio.service.service;

import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;

import java.util.Set;
import java.util.Date;

public interface EntitiesHandlerService {

    Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClass (String node, String tripleStore, String className, boolean searchInOtherNodes, Date deltaDate);

    SimilarityResult findEntitiesLinksByNodeAndTripleStoreAndTripleObject (TripleObject tripleObject, boolean searchInOtherNodes);
}
