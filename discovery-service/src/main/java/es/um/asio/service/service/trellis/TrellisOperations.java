package es.um.asio.service.service.trellis;

import es.um.asio.service.model.rdf.TripleObjectLink;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface TrellisOperations {

    /**
     * Exists container.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param containerName: Container name
     * @return true, if successful
     */
    boolean existsContainer(String path, String containerName, boolean isFullURI);

    /**
     * Creates the container.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param containerName: Container name
     */
    String saveContainer(String path, String containerName, boolean isFullURI);

    /**
     * Creates the container.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param containerName: Container name
     */
    boolean deleteContainer(String path, String containerName, boolean isFullURI);

    /**
     * Creates the container.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param containerName: Container name
     */
    Model getContainer(String path, String containerName, boolean isFullURI);


    /**
     * Creates/Update entry in idempotent way.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param  tripleObjectLink the triple object to link
     */
    String saveEntry(String path, TripleObjectLink tripleObjectLink, boolean isFullURI);


    /**
     * Delete entry.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param  tripleObjectLink the triple object to link
     */
    boolean deleteEntry(String path, TripleObjectLink tripleObjectLink, boolean isFullURI);

    /**
     * Delete entry.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param  entityId:  The id of entity
     */
    Model getEntry(String path, String entityId,boolean isFullURI);

    /**
     * Creates/Update entry in idempotent way.
     *
     * @param path: isFullURI if false compose the URI from  baseURI and Path else path is the full URI
     * @param path: path for resurce
     * @param  tripleObjectLink the triple object to link
     */
    String addPropertyToEntity(String path, TripleObjectLink tripleObjectLink, List<Pair<String,String>> properties, boolean isFullURI);


}
