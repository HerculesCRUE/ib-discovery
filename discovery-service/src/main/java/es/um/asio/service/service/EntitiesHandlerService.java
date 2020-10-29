package es.um.asio.service.service;

import es.um.asio.service.model.TripleObject;

import java.util.List;

public interface EntitiesHandlerService {

    List<TripleObject> findEntitiesLinksByNodeAndTripleStoreAndClass (String node, String tripleStore, String className);
}
