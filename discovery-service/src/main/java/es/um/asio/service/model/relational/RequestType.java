package es.um.asio.service.model.relational;

/**
 * Enumerated Class. In relational model the Request type.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public enum RequestType {
    ENTITY_LINK_CLASS,ENTITY_LINK_INSTANCE,LOD_SEARCH;

    static RequestType getFromString(String rt) {
        if (rt == null)
            return null;
        switch (rt) {
            case "ENTITY_LINK_CLASS":
                return RequestType.ENTITY_LINK_CLASS;
            case "ENTITY_LINK_INSTANCE":
                return RequestType.ENTITY_LINK_INSTANCE;
            case "LOD_SEARCH":
                return RequestType.LOD_SEARCH;
            default:
                return null;
        }
    }
}
