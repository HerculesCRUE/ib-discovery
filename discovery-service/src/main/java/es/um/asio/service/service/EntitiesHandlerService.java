package es.um.asio.service.service;

import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;

import java.util.Set;

public interface EntitiesHandlerService {

    Set<SimilarityResult> findEntitiesLinksByNodeAndTripleStoreAndClass (String node, String tripleStore, String className);

    SimilarityResult findEntitiesLinksByNodeAndTripleStoreAndTripleObject (TripleObject tripleObject);
}
