package es.um.asio.service.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


public interface DataHandler {

    public CompletableFuture<Boolean> populateData() throws ParseException, IOException, URISyntaxException;

}
