package es.um.asio.service.repository.triplestore;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;


public class TrellisHandler extends TripleStoreHandler {

    private final Logger logger = LoggerFactory.getLogger(TrellisHandler.class);
    private String nodeName;
    private String baseURL;
    private String user;
    private String password;
    private TripleStore tripleStore;
    Map<String,String> headers = new HashMap<>();

    public TrellisHandler(String nodeName, String baseURL, String user, String password) {
        this.nodeName = nodeName;
        this.baseURL = baseURL;
        this.user = user;
        this.password = password;
        this.tripleStore = new TripleStore("trellis",this.nodeName,this.baseURL,this.user,this.password);
        headers.put("Accept","application/ld+json");
        headers.put("Prefer","return=representation; include=http://www.trellisldp.org/ns/trellis#PreferAudit");
        headers.put("Authorization",getBasicAuthentication());
    }

    @Override
    public void updateData(CacheServiceImp cacheService) throws IOException, URISyntaxException, ParseException {
        Set<TripleObject> triplesMapCached = cacheService.getAllTripleObjects();
        int instancesCounter = 0;
        int changes = 0;
        // Do request to Base URL
        Response rMain = doRequest(this.baseURL);
        JsonObject jBaseObject = new Gson().fromJson(rMain.body().string(), JsonObject.class);
        logger.info("Processing Node {}", nodeName);
        if (jBaseObject.has("contains")) {
            int classesCounter = 0;
            int totalClasses = jBaseObject.get("contains").getAsJsonArray().size();
            // Para cada una de las clases contenidas en la Base URL
            for (JsonElement jeClass : jBaseObject.get("contains").getAsJsonArray()) {
                String classURL = jeClass.getAsString();
                String className = classURL.replace(this.baseURL,"").replaceAll("/", "");
                logger.info("	Processing Class {} ({}/{})", className,++classesCounter,totalClasses);
                // Request to Class URL
                Response rClass = doRequest(classURL);
                JsonObject jClassObject = new Gson().fromJson(rClass.body().string(), JsonObject.class);
                // Si contiene instancias
                if (jClassObject.has("@graph")) {
                    // Para cada una de las instancias de la clase
                    int instancesInClass = 0;
                    for (JsonElement jeGraphClass : jClassObject.get("@graph").getAsJsonArray()) {
                        JsonObject joGraphClass = jeGraphClass.getAsJsonObject();
                        if (joGraphClass.has("@id") && joGraphClass.has("@type") && joGraphClass.has("contains")) {

                            if (!joGraphClass.get("contains").isJsonArray()) {
                                JsonArray jContains = new JsonArray();
                                jContains.add(joGraphClass.get("contains").getAsString());
                                joGraphClass.add("contains",jContains);
                            }
                            int totalInClass = joGraphClass.get("contains").getAsJsonArray().size();
                            for (JsonElement jeInstance : joGraphClass.get("contains").getAsJsonArray()) {
                                String instanceURL = jeInstance.getAsString();
                                String instanceId = instanceURL.replace(classURL,"").replace("/","");
                                TripleObject to = cacheService.getTripleObject(nodeName,"trellis",className,instanceId);
                                // Si la cache contiene la instancia, no hag la petición
                                boolean isNew = (to==null);
                                if (to == null) {
                                    // En caso contrario, hago la petición para añadir a la cache
                                    // Request to Instance URL
                                    Response rInstance = doRequest(instanceURL);
                                    JsonObject jInstanceObject = new Gson().fromJson(rInstance.body().string(), JsonObject.class);
                                    String lastModification = rInstance.headers().get("Last-Modified");

                                    if (jClassObject.has("@graph")) {
                                        for (JsonElement jeGraphInstance : jInstanceObject.get("@graph").getAsJsonArray()) {
                                            JsonObject joGraphInstance = jeGraphInstance.getAsJsonObject();

                                            if (joGraphInstance.has("@id") && joGraphInstance.get("@id").isJsonPrimitive() && joGraphInstance.get("@id").getAsString().equals(instanceURL)) {
                                                joGraphInstance.remove("@id");
                                                joGraphInstance.remove("@type");
                                                to = new TripleObject(this.tripleStore, joGraphInstance, className, instanceId, lastModification);
                                                changes++;
                                            }
                                        }
                                    }
                                } else {
                                    triplesMapCached.remove(to);
                                }
                                if (to != null) {
                                    to.setTripleStore(this.tripleStore);
                                    String nText = ((isNew)?"(New) ":" ");
                                    logger.info("		Processing Node {} Instances: {} ({}/{}): {}	,class ({}/{}):{}	,id: {}	,data:{}",nText, ++instancesCounter, ++instancesInClass,totalInClass, nodeName, classesCounter,totalClasses,className, instanceId, to.toString());
                                    cacheService.addTripleObject(nodeName,"trellis", to);
                                }

                            }
                        }
                    }
                }

            } // End of class
        }

        if (changes > 0 || triplesMapCached.size() > 0) {
            if (triplesMapCached.size() > 0) {
                for (TripleObject to : triplesMapCached) {
                    cacheService.removeTripleObject(this.nodeName, this.tripleStore.getTripleStore(), to);
                }
            }
            logger.info(String.format("Found %d changes in Trellis and %d instances will be deleted. Saving in Cache and Redis. The cache is updated",changes,triplesMapCached.size()));
            cacheService.saveTriplesMapInCache();
        } else {
            logger.info(String.format("No changes Found in Trellis. The cache is updated"));
        }
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
