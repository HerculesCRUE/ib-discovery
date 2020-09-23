package es.um.asio.service.repository.triplestore;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;

public abstract class TripleStoreHandler {

    public abstract void populateData() throws IOException, URISyntaxException, ParseException;

    public static TripleStoreHandler getHandler(String type, String node, String baseURL, String user, String password, Date filterDate) {
        if (type.trim().toLowerCase().equals("trellis"))
            return new TrellisHandler(node,baseURL,user,password,filterDate);
        else
            throw new IllegalArgumentException("Not exist correct handler for type: "+ type);
    }
}
