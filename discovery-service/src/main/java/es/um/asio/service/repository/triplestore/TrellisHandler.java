package es.um.asio.service.repository.triplestore;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TrellisHandler extends TripleStoreHandler {

    @Autowired
    CacheServiceImp cacheService;

    private final Logger logger = LoggerFactory.getLogger(TrellisHandler.class);
    private String nodeName;
    private String baseURL;
    private Date filterDate;
    private String user;
    private String password;
    Map<String,String> headers = new HashMap<>();

    public TrellisHandler(String nodeName, String baseURL, String user, String password, Date filterDate) {
        this.nodeName = nodeName;
        this.baseURL = baseURL;
        this.filterDate = filterDate;
        this.user = user;
        this.password = password;
        headers.put("Accept","application/ld+json");
        headers.put("Prefer","return=representation; include=http://www.trellisldp.org/ns/trellis#PreferAudit");
        headers.put("Authorization",getBasicAuthentication());
    }

    @Override
    public void populateData() throws IOException, URISyntaxException, ParseException {

    }

    private String getBasicAuthentication() {
        return "Basic " + Base64.getEncoder().encodeToString((this.user+":"+this.password).getBytes());
    }

    private Response doRequest(String url) throws RequestAbortedException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .headers(buildHeaders())
                    .build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in request",e);
            throw new RequestAbortedException("Error doing request");
        }
    }

    private Headers buildHeaders() {
        return Headers.of(this.headers);
    }

}
