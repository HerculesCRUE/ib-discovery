package es.um.asio.service.repository.triplestore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.BasicAction;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
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

    private static final String TRIPLE_STORE = "trellis";
    private static final String CONTAINS = "contains";
    private static final String GRAPH = "@graph";
    private static final String TYPE = "@type";
    private static final String CONTEXT = "@context";
    private static final String J_REGEX = "j\\.[0-9]+:";
    private static final String ID = "@id";
    private static final String VALUE = "@value";

    public TrellisHandler(String nodeName, String baseURL, String user, String password) {
        this.nodeName = nodeName;
        this.baseURL = baseURL;
        this.user = user;
        this.password = password;
        this.tripleStore = new TripleStore(TRIPLE_STORE,this.nodeName,this.baseURL,this.user,this.password);
        headers.put("Accept","application/ld+json");
        headers.put("Prefer","return=representation; include=http://www.trellisldp.org/ns/trellis#PreferAudit");
        headers.put("Authorization",getBasicAuthentication());
    }

    @Override
    public boolean updateData(CacheServiceImp cacheService) throws IOException, URISyntaxException, ParseException {
        Set<TripleObject> triplesMapCached = cacheService.getAllTripleObjects(this.nodeName,this.tripleStore.getTripleStore());
        int instancesCounter = 0;
        int changes = 0;
        // Do request to Base URL
        Response rMain = doRequest(this.baseURL); // Request Classes
        if (rMain == null) {
            throw new CustomDiscoveryException(String.format("Request at %s response is null", this.baseURL));
        }
        JsonObject jBaseObject = new Gson().fromJson(rMain.body().string(), JsonObject.class);
        logger.info("Processing Node {}", nodeName);
        if (rMain!= null && jBaseObject.has(CONTAINS)) {
            int classesCounter = 0;
            int totalClasses = jBaseObject.get(CONTAINS).getAsJsonArray().size();
            // Para cada una de las clases contenidas en la Base URL
            for (JsonElement jeClass : jBaseObject.get(CONTAINS).getAsJsonArray()) { // Comienzo clases
                String classURL = jeClass.getAsString();
                String className = classURL.replace(this.baseURL,"").replaceAll("/", "");
                int classChanges = 0;

                logger.info("	Processing Class {} ({}/{})", className,++classesCounter,totalClasses);
                // Request to Class URL
                Response rClass = doRequest(classURL); // Request
                if (rClass == null)
                    throw new CustomDiscoveryException(String.format("Response at request at %s is null" ,classURL));
                JsonObject jClassObject = new Gson().fromJson(rClass.body().string(), JsonObject.class);
                // Si contiene instancias
                if (rClass!=null && jClassObject.has(GRAPH)) {
                    // Para cada una de las instancias de la clase
                    int instancesInClass = 0;
                    for (JsonElement jeGraphClass : jClassObject.get(GRAPH).getAsJsonArray()) {
                        JsonObject joGraphClass = jeGraphClass.getAsJsonObject();
                        if (joGraphClass.has(ID) && joGraphClass.has(TYPE) && joGraphClass.has(CONTAINS)) {

                            if (!joGraphClass.get(CONTAINS).isJsonArray()) {
                                JsonArray jContains = new JsonArray();
                                jContains.add(joGraphClass.get(CONTAINS).getAsString());
                                joGraphClass.add(CONTAINS,jContains);
                            }
                            int totalInClass = joGraphClass.get(CONTAINS).getAsJsonArray().size();
                            for (JsonElement jeInstance : joGraphClass.get(CONTAINS).getAsJsonArray()) {
                                String instanceURL = jeInstance.getAsString();
                                String instanceId = instanceURL.replace(classURL,"").replace("/","");

                                TripleObject to = cacheService.getTripleObject(nodeName,TRIPLE_STORE,className,instanceId);
                                // Si la cache contiene la instancia, no hag la petición
                                boolean isNew = (to==null);
                                if (to == null || to.getAttributes() == null || to.getAttributes().size() == 0 || !Utils.isValidString(to.getLocalURI()) /*|| to.getId().contains("f457c545-a9de-388f-98ec-ee47145a72c0")*/) {
                                    // En caso contrario, hago la petición para añadir a la cache
                                    // Request to Instance URL
                                    Response rInstance = doRequest(instanceURL);
                                    if (rInstance!=null) {
                                        JsonObject jInstanceObject = new Gson().fromJson(rInstance.body().string(), JsonObject.class);
                                        String lastModification = rInstance.headers().get("Last-Modified");

                                        if (jClassObject.has(GRAPH) && jClassObject.has(CONTEXT)) {
                                            JsonObject jContext = jClassObject.get(CONTEXT).getAsJsonObject();
                                            String jStrInstance = jInstanceObject.toString().replace(J_REGEX,"");
                                            JsonObject jeClassInstance = new Gson().fromJson(jStrInstance, JsonObject.class);

                                            JsonObject jRootObject = parseJsonDataByCvn(jeClassInstance.get(GRAPH).getAsJsonArray(),jContext, className, instanceId);
                                            to = new TripleObject(this.tripleStore, jRootObject, className, instanceId,instanceURL, lastModification);
                                            changes++;
                                            classChanges ++;
                                        }
                                    }
                                } else {
                                    triplesMapCached.remove(to);
                                }
                                if (to != null) {
                                    try {
                                        to.setTripleStore(this.tripleStore);
                                        String nText = ((isNew)?"(New) ":" ");
                                        logger.info("		Processing Node {} Instances: {} ({}/{}): {}	,class ({}/{}):{}	,id: {}	,data:{}",nText, ++instancesCounter, ++instancesInClass,totalInClass, nodeName, classesCounter,totalClasses,className, instanceId, to);
                                        cacheService.addTripleObject(nodeName,TRIPLE_STORE, to);
                                    } catch (Exception e) {
                                        logger.error("Error processing Node {}: {}",nodeName,e.getMessage());
                                    }
                                }

                            }
                        }
                    }
                    if (classChanges>0)
                        cacheService.saveTriplesMapInCache(nodeName,TRIPLE_STORE,className);
                }

            } // End of class
        }

        if (changes > 0 || triplesMapCached.isEmpty()) {
            if (triplesMapCached.isEmpty()) {
                for (TripleObject to : triplesMapCached) {
                    cacheService.removeTripleObject(this.nodeName, this.tripleStore.getTripleStore(), to);
                }
            }
            logger.info("Found {} changes in Trellis and {} instances will be deleted. Saving in Cache and Redis. The cache is updated", changes,triplesMapCached.size());
            return true;
        } else {
            logger.info("No changes Found in Trellis. The cache is updated");
            return false;
        }
    }

    @Override
    public boolean updateTripleObject(CacheServiceImp cacheService,String node, String tripleStore, String className,String localURI, BasicAction basicAction) throws IOException, URISyntaxException, ParseException {
        Response rClass = doRequest(this.baseURL+className); // Request
        if (rClass == null)
            throw new CustomDiscoveryException(String.format("Response at request %s is null",this.baseURL+className));
        JsonObject jClassObject = new Gson().fromJson(rClass.body().string(), JsonObject.class);

        String[] urisParts = localURI.split("/");
        String instanceId;
        if (Utils.isValidString(urisParts[urisParts.length-1]))
            instanceId = urisParts[urisParts.length-1];
        else
            instanceId = urisParts[urisParts.length-2];

        TripleObject to = cacheService.getTripleObject(nodeName,TRIPLE_STORE,className,instanceId);
        if (basicAction.equals(BasicAction.DELETE)) {
            if (to!=null) {
                cacheService.removeTripleObject(node, tripleStore, to);
            }
            return true;
        } else {
            Response rInstance = doRequest(localURI);
            if (rInstance!=null) {
                JsonObject jInstanceObject = new Gson().fromJson(rInstance.body().string(), JsonObject.class);
                String lastModification = rInstance.headers().get("Last-Modified");

                if (jClassObject.has(GRAPH) && jClassObject.has(CONTEXT)) {
                    JsonObject jContext = jClassObject.get(CONTEXT).getAsJsonObject();
                    String jStrInstance = jInstanceObject.toString().replace(J_REGEX,"");
                    JsonObject jeClassInstance = new Gson().fromJson(jStrInstance, JsonObject.class);

                    JsonObject jRootObject = parseJsonDataByCvn(jeClassInstance.get(GRAPH).getAsJsonArray(),jContext, className, instanceId);
                    TripleStore ts = new TripleStore(tripleStore,node);
                    to = new TripleObject(ts, jRootObject, className, instanceId,localURI, lastModification);
                    cacheService.addTripleObject(node,tripleStore,to);
                    return true;
                }
            }
        }
        return false;

    }

    private JsonObject parseJsonDataByCvn(JsonArray jData, JsonObject jContext, String className, String id) {
        try {
            String uuid = id.substring(id.lastIndexOf('_') + 1);
            Map<String, Object> attrs = new LinkedTreeMap<>();
            JsonObject jRootObject = null;
            for (JsonElement jeAttribute : jData) {
                JsonObject jAttribute = cleanAttrs(jeAttribute);
                if (
                        (
                                jAttribute.has(ID) && jAttribute.get(ID).isJsonPrimitive() && jAttribute.get(ID).getAsString().contains(uuid)
                        ) && (
                                jAttribute.has(TYPE) && jAttribute.get(TYPE).isJsonPrimitive() && (jAttribute.get(TYPE).getAsString().contains(className)

                            ||
                            (
                                jAttribute.has(TYPE) && jAttribute.get(TYPE).isJsonPrimitive() &&
                                jAttribute.get(TYPE).getAsString().matches((J_REGEX+".*")) &&
                                //jContext.has(jAttribute.get(TYPE).getAsString()) &&
                                Utils.checkIfComposeStringIsSame(jAttribute.get(TYPE).getAsString(),className)
                            )
                            )
                        )
                ) {
                    jAttribute.remove(ID);
                    jAttribute.remove(TYPE);
                    jRootObject = jAttribute.deepCopy();
                } else { // Si no es el objeto raíz
                    if (
                            jAttribute.has(ID) && jAttribute.get(ID).isJsonPrimitive() && jAttribute.get(ID).getAsString().startsWith("_:b")
                    ) {
                        String jId = jAttribute.get(ID).getAsString();
                        jAttribute.remove(ID);
                        jAttribute.remove(TYPE);
                        attrs.put(jId, jAttribute);
                    }
                }
            }

            if (jRootObject != null)
                jRootObject = buildCvnFromRoot(id, jContext, jRootObject, attrs);
            else {
                jRootObject = new Gson().fromJson(new ObjectMapper().writeValueAsString(attrs), JsonObject.class);
            }
            return jRootObject;
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    private JsonObject buildCvnFromRoot(String id, JsonObject jContext, JsonObject jRoot, Map<String,Object> attrs ){
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, JsonElement> jeAtt : jRoot.entrySet()) {
            if (jeAtt.getValue().isJsonPrimitive()) { // Si es primitivo
                if (jeAtt.getValue().getAsString().startsWith("_:b")) {
                    if (attrs.containsKey(jeAtt.getValue().getAsString())) {
                        JsonObject jContent = (JsonObject) attrs.get(jeAtt.getValue().getAsString());
                        if (jContent.size()>=0)
                            jeAtt.setValue(buildCvnFromRoot(id,jContext,jContent,attrs));
                        else
                            jeAtt.setValue(jContent);
                    }
                } else if (jeAtt.getValue().getAsString().strip().trim().equals("")) {
                    toRemove.add(jeAtt.getKey());
                }
            } else if (jeAtt.getValue().isJsonArray()) { // Si es array
                JsonArray jInners = new JsonArray();
                for (JsonElement jeAttInner : jeAtt.getValue().getAsJsonArray()) {
                    if (jeAttInner.isJsonPrimitive() && jeAttInner.getAsString().startsWith("_:b") && attrs.containsKey(jeAttInner.getAsString())) {
                        JsonObject jContentInner = (JsonObject) attrs.get(jeAttInner.getAsString());
                        jInners.add(buildCvnFromRoot(id,jContext,jContentInner,attrs));
                    }
                }
                jeAtt.setValue(jInners);
            } else if(jeAtt.getValue().isJsonObject()) {
                JsonObject jData = jeAtt.getValue().getAsJsonObject();
                // Elimino objetos de tipo @Language, @value
                if (jData.size()==2 && jData.has("@language") && jData.has(VALUE)) {
                    if (!jData.get(VALUE).getAsString().strip().trim().equals(""))
                        jeAtt.setValue(jData.get(VALUE));
                    else
                        toRemove.add(jeAtt.getKey());
                } else {
                    jeAtt.setValue(buildCvnFromRoot(id, jContext,jeAtt.getValue().getAsJsonObject(), attrs));
                }
            }
            if (jeAtt.getValue().isJsonPrimitive() && jeAtt.getValue().getAsString().startsWith("j.")) {
                String replaced = Utils.replaceSubstringByRegex(jeAtt.getValue().getAsString(),":",jContext,"j\\.[0-9]+");
                jeAtt.setValue(new JsonPrimitive(replaced));
            }
        }
        for (String remove : toRemove) {
            jRoot.remove(remove);
        }
        return jRoot;
    }

    /*
     * Clean prefix in attributes with shape of URL or trellis prefix of type j.number:
     */
    private JsonObject cleanAttrs(JsonElement je) {
        String regex = J_REGEX;
        JsonObject jo = new JsonObject();
        for (Map.Entry<String, JsonElement> joInner : je.getAsJsonObject().entrySet()) {
            String key = joInner.getKey();
            if (Utils.containsRegex(key,regex+".*")) {
                key = key.replaceAll(regex,"");
            }
            if (Utils.isValidURL(key)) {
                key = Utils.getLastFragmentURL(key);
            }
            jo.add(key,joInner.getValue());
        }
        return jo;
    }

    private String getBasicAuthentication() {
        return "Basic " + Base64.getEncoder().encodeToString((this.user+":"+this.password).getBytes());
    }

    @Nullable
    private Response doRequest(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .headers(buildHeaders())
                    .build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            logger.error("Error in request",e);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException interruptedException) {
                logger.error(String.format("Error in request: %s",interruptedException.getMessage()));
            }
            return null;
        }
    }

    private Headers buildHeaders() {
        return Headers.of(this.headers);
    }

}
