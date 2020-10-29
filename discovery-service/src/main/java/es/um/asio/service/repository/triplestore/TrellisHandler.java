package es.um.asio.service.repository.triplestore;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.util.Utils;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public boolean updateData(CacheServiceImp cacheService) throws IOException, URISyntaxException, ParseException {
        Set<TripleObject> triplesMapCached = cacheService.getAllTripleObjects();
        int instancesCounter = 0;
        int changes = 0;
        // Do request to Base URL
        Response rMain = doRequest(this.baseURL); // Request Classes
        JsonObject jBaseObject = new Gson().fromJson(rMain.body().string(), JsonObject.class);
        logger.info("Processing Node {}", nodeName);
        if (rMain!= null & jBaseObject.has("contains")) {
            int classesCounter = 0;
            int totalClasses = jBaseObject.get("contains").getAsJsonArray().size();
            // Para cada una de las clases contenidas en la Base URL
            for (JsonElement jeClass : jBaseObject.get("contains").getAsJsonArray()) { // Comienzo clases
                String classURL = jeClass.getAsString();
                String className = classURL.replace(this.baseURL,"").replaceAll("/", "");
                int classChanges = 0;

                logger.info("	Processing Class {} ({}/{})", className,++classesCounter,totalClasses);
                // Request to Class URL
                Response rClass = doRequest(classURL); // Request
                JsonObject jClassObject = new Gson().fromJson(rClass.body().string(), JsonObject.class);
                // Si contiene instancias
                if (rClass!=null && jClassObject.has("@graph")) {
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
/*                                if (instanceId.equals("http:_hercules.org_um_es-ES_rec_CvnRootBean_d4bbead1-0527-422d-9a40-fd0a904d06c5")) {
                                    System.out.println(); // Remove
                                }*/
                                TripleObject to = cacheService.getTripleObject(nodeName,"trellis",className,instanceId);
                                // Si la cache contiene la instancia, no hag la petición
                                boolean isNew = (to==null);
                                if (to == null || (className.equals("Proyecto") /*&& to.getAttributes().size() < 2*/)) {
                                    // En caso contrario, hago la petición para añadir a la cache
                                    // Request to Instance URL

                                    Response rInstance = doRequest(instanceURL);
                                    if (rInstance!=null) {
                                        JsonObject jInstanceObject = new Gson().fromJson(rInstance.body().string(), JsonObject.class);
                                        String lastModification = rInstance.headers().get("Last-Modified");
                                        if (jClassObject.has("@graph")) {
/*                                            if (className.contains("CvnRootBean") || true) {*/
                                            String jStrInstance = jInstanceObject.toString().replace("j\\.[0-9]+:","");
                                            JsonObject jeClassInstance = new Gson().fromJson(jStrInstance, JsonObject.class);

                                            JsonObject jRootObject = parseJsonDataByCvn(jeClassInstance.get("@graph").getAsJsonArray(), className, instanceId);
                                            //to = new TripleObject(this.tripleStore, jInstanceObject.get("@graph").getAsJsonArray(), className, instanceId, lastModification);
                                            to = new TripleObject(this.tripleStore, jRootObject, className, instanceId, lastModification);
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
                                        logger.info("		Processing Node {} Instances: {} ({}/{}): {}	,class ({}/{}):{}	,id: {}	,data:{}",nText, ++instancesCounter, ++instancesInClass,totalInClass, nodeName, classesCounter,totalClasses,className, instanceId, to.toString());
                                        cacheService.addTripleObject(nodeName,"trellis", to);
                                    } catch (Exception e) {
                                        System.out.println();
/*                                        to.setTripleStore(this.tripleStore);
                                        String nText = ((isNew)?"(New) ":" ");
                                        logger.info("		Processing Node {} Instances: {} ({}/{}): {}	,class ({}/{}):{}	,id: {}	,data:{}",nText, ++instancesCounter, ++instancesInClass,totalInClass, nodeName, classesCounter,totalClasses,className, instanceId, to.toString());
                                        cacheService.addTripleObject(nodeName,"trellis", to);*/
                                    }
                                }

                            }
                        }
                    }
                    if (classChanges>0)
                        cacheService.saveTriplesMapInCache(nodeName,"trellis",className);
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
            return true;
        } else {
            logger.info(String.format("No changes Found in Trellis. The cache is updated"));
            return false;
        }
    }

    private JsonObject parseJsonDataByCvn(JsonArray jData, String className, String id) {
/*        if (id.equals("http:_hercules.org_um_es-ES_rec_CvnRootBean_d4bbead1-0527-422d-9a40-fd0a904d06c5"))
            System.out.println(); // Remove*/
        jData.toString().replace("j\\.[0-9]+:","");
        String uuid = id.substring(id.lastIndexOf("_")+1);
        Map<String,Object> attrs = new LinkedTreeMap<>();
        JsonObject jRootObject = null;
        for (JsonElement jeAttribute : jData) {
            JsonObject jAttribute = cleanAttrs(jeAttribute);
            if (
                    jAttribute.has("@id") && jAttribute.get("@id").isJsonPrimitive() && jAttribute.get("@id").getAsString().contains(uuid) &&
                            jAttribute.has("@type") && jAttribute.get("@type").isJsonPrimitive() && jAttribute.get("@type").getAsString().contains(className)
            ) {
                jAttribute.remove("@id");
                jAttribute.remove("@type");
                jRootObject = jAttribute.deepCopy();
            } else { // Si no es el objeto raiz
                if (
                        jAttribute.has("@id") && jAttribute.get("@id").isJsonPrimitive() && jAttribute.get("@id").getAsString().startsWith("_:b")
                ) {
                    String jId = jAttribute.get("@id").getAsString();
                    jAttribute.remove("@id");
                    jAttribute.remove("@type");
                    attrs.put(jId,jAttribute);
                }
            }
        }

        if (jRootObject!=null)
            jRootObject = buildCvnFromRoot(jRootObject.deepCopy(),jRootObject,attrs);
        return jRootObject;
    }

    private JsonObject buildCvnFromRoot(JsonObject jParentRoot,JsonObject jRoot, Map<String,Object> attrs ){
        for (Map.Entry<String, JsonElement> jeAtt : jRoot.entrySet()) {
            String key = jeAtt.getKey();
/*            if (key.contains("cvnFamilyNameBean"))
                System.out.println();*/

            if (jeAtt.getValue().isJsonPrimitive()) { // Si es primitivo
                if (jeAtt.getValue().getAsString().startsWith("_:b")) {
                    if (attrs.containsKey(jeAtt.getValue().getAsString())) {
                        JsonObject jContent = (JsonObject) attrs.get(jeAtt.getValue().getAsString());
                        if (jContent.size()>=0)
                            jeAtt.setValue(buildCvnFromRoot(jRoot,jContent,attrs));
                        else
                            jeAtt.setValue(jContent);
                    }
                }
            } else if (jeAtt.getValue().isJsonArray()) { // Si es array
                JsonArray jInners = new JsonArray();
                for (JsonElement jeAttInner : jeAtt.getValue().getAsJsonArray()) {
                    if (jeAttInner.isJsonPrimitive()) { // Si es primitivo
                        if (jeAttInner.getAsString().startsWith("_:b")) {
                            if (attrs.containsKey(jeAttInner.getAsString())) {
                                JsonObject jContentInner = (JsonObject) attrs.get(jeAttInner.getAsString());
                                jInners.add(buildCvnFromRoot(jRoot,jContentInner,attrs));
                            }
                        }
                    }
                }
                jeAtt.setValue(jInners);
            }
        }
        return jRoot;
    }

    /*
     * Clean prefix in attributes with shape of URL or trellis prefix of type j.number:
     */
    private JsonObject cleanAttrs(JsonElement je) {
        String regex = "j\\.[0-9]+:";
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
            try {
                Thread.sleep(10000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            return null;
        }
    }

    private Headers buildHeaders() {
        return Headers.of(this.headers);
    }

}
