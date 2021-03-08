package es.um.asio.service.service;

import es.um.asio.service.model.BasicAction;
import es.um.asio.service.model.TripleObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

/**
 * DataHandler interface. For handle fetch data
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface DataHandler {

    /**
     * Call to handle populate data from REDIS cache, Triple Stores and save in ELASTICSEARCH
     * @see CompletableFuture
     * @return CompletableFuture, in async way. True is is completed else False
     * @throws ParseException Exception if error in parse data
     * @throws IOException Exception on In Out operations
     * @throws URISyntaxException Exception launch on URIs syntax exception
     */
    public CompletableFuture<Boolean> populateData() throws ParseException, IOException, URISyntaxException;

    /**
     * Call when a new data is available
     * @param nodeName String. Node of the data to update
     * @param tripleStore String. Triple Store of the data to update
     * @param className String. Class of the data to update
     * @param entityURI String. localUri where the data is stored
     * @param basicAction String. One of this INSERT, UPDATE or DELETE
     * @return CompletableFuture
     * @throws ParseException Exception if error in parse data
     * @throws IOException Exception on In Out operations
     * @throws URISyntaxException Exception launch on URIs syntax exception
     */
    public CompletableFuture<Boolean> actualizeData(String nodeName, String tripleStore, String className, String entityURI, BasicAction basicAction) throws ParseException, IOException, URISyntaxException;

}
