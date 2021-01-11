package es.um.asio.service.service;

import es.um.asio.service.model.URIComponent;

public interface SchemaService {

    public URIComponent getURIComponentFromCanonicalURI(String uri);

    public URIComponent getURIComponentFromCanonicalLocalURI(String uri);

}
