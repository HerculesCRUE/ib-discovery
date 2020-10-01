package es.um.asio.service.service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;


public interface DataHandler {

    public CompletableFuture<Boolean> populateData();

}
