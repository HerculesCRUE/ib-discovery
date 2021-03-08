package es.um.asio.service.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.URIComponent;

/**
 * SchemaService interface. For handle operations with the URIs Factory
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface SchemaService {

    /**
     * break down into URIComponent a URI created by URIS factory for Canonical URIs
     * @see URIComponent
     * @param uri
     * @return
     */
    public URIComponent getURIComponentFromCanonicalURI(String uri);

    /**
     * break down into URIComponent a URI created by URIS factory for Canonical Local URIs
     * @see URIComponent
     * @param uri
     * @return
     */
    public URIComponent getURIComponentFromCanonicalLocalURI(String uri);

    /**
     * Create a Canonical URI from Triple Object for a RESOURCE
     * @see TripleObject
     * @param to TripleObject. The triple Object
     * @param type String. The type attribute in URI
     * @param language String. The language attribute in URI
     * @param tripleStore String. The triple store attribute in URI
     * @param requestDiscovery boolean. If true do a request to URIs Factory
     * @return JsonObject with the response of the URIs factory
     */
    public JsonObject createCanonicalURIFromResource(TripleObject to, String type, String language, String tripleStore, boolean requestDiscovery);

    /**
     * Create a Canonical URI from Triple Object for a ENTITY
     * @see TripleObject
     * @param to TripleObject. The triple Object
     * @param type String. The type attribute in URI
     * @param language String. The language attribute in URI
     * @param tripleStore String. The triple store attribute in URI
     * @param requestDiscovery boolean. If true do a request to URIs Factory
     * @return JsonObject with the response of the URIs factory
     */
    public JsonObject createCanonicalURIFromEntity(String className, String canonicalClassName, String type, String language);

    /**
     * Create a Canonical URI from Triple Object for a PROPERTY
     * @see TripleObject
     * @param to TripleObject. The triple Object
     * @param type String. The type attribute in URI
     * @param language String. The language attribute in URI
     * @param tripleStore String. The triple store attribute in URI
     * @param requestDiscovery boolean. If true do a request to URIs Factory
     * @return JsonObject with the response of the URIs factory
     */
    public JsonObject createCanonicalURIFromProperty(String className, String canonicalClassName , String type, String language);

    /**
     * Create a link of Local URI and canonical URI
     * @see TripleObject
     * @param to TripleObject. The triple Object
     * @param canonicalLanguageURI String. The canonical URI
     * @param localURI String. The local URI
     * @param storageName String. The storage name
     * @return JsonObject with the response of the URIs factory
     */
    public JsonArray linkCanonicalToLocalURI(String canonicalLanguageURI, String localURI , String storageName );

}
