package es.um.asio.service.service;

import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.model.TripleObject;

import java.util.List;
import java.util.Map;

public interface EntitiesHandlerService {

    Map<String, List<EntitySimilarityObj>> findEntitiesLinksByNodeAndTripleStoreAndClass (String node, String tripleStore, String className);
}
