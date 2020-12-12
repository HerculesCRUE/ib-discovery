package es.um.asio.service.repository.triplestore;

import es.um.asio.service.model.BasicAction;
import es.um.asio.service.service.impl.CacheServiceImp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

public abstract class TripleStoreHandler {

    public abstract boolean updateData(CacheServiceImp cacheService) throws IOException, URISyntaxException, ParseException;

    public abstract boolean updateTripleObject(CacheServiceImp cacheService,String node, String tripleStore, String className,String localURI, BasicAction basicAction) throws IOException, URISyntaxException, ParseException;

    public static TripleStoreHandler getHandler(String type, String node, String baseURL, String user, String password) {
        if (type.trim().equalsIgnoreCase("trellis"))
            return new TrellisHandler(node,baseURL,user,password);
        else
            throw new IllegalArgumentException("Not exist correct handler for type: "+ type);
    }
}
