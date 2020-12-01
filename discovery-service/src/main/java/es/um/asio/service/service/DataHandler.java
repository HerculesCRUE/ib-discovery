package es.um.asio.service.service;

import es.um.asio.service.model.BasicAction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;


public interface DataHandler {

    public CompletableFuture<Boolean> populateData() throws ParseException, IOException, URISyntaxException;

    public CompletableFuture<Boolean> actualizeData(String nodeName, String tripleStore, String className, String entityURI, BasicAction basicAction) throws ParseException, IOException, URISyntaxException;

}
