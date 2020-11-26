package es.um.asio.service.service;

import es.um.asio.service.model.Action;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


public interface DataHandler {

    public CompletableFuture<Boolean> populateData() throws ParseException, IOException, URISyntaxException;

    public CompletableFuture<Boolean> actualizeData(String nodeName, String tripleStore, String className, String entityURI, Action action) throws ParseException, IOException, URISyntaxException;

}
