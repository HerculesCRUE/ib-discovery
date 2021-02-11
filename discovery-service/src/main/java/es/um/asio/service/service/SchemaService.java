package es.um.asio.service.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.URIComponent;

public interface SchemaService {

    public URIComponent getURIComponentFromCanonicalURI(String uri);

    public URIComponent getURIComponentFromCanonicalLocalURI(String uri);

    public JsonObject createCanonicalURIFromResource(TripleObject to, String type, String language, String tripleStore, boolean requestDiscovery);

    public JsonObject createCanonicalURIFromEntity(String className, String canonicalClassName, String type, String language);

    public JsonObject createCanonicalURIFromProperty(String className, String canonicalClassName , String type, String language);

    public JsonArray linkCanonicalToLocalURI(String canonicalLanguageURI, String localURI , String storageName );

}
