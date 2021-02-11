package es.um.asio.service.service.impl;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;
import com.squareup.okhttp.*;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.URIComponent;

import es.um.asio.service.service.SchemaService;
import es.um.asio.service.util.Utils;
import org.apache.http.client.methods.RequestBuilder;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchemaServiceImp implements SchemaService {

    @Value("${data-sources.urisFactoryHost}")
    public String urisFactoryHost;

    @Value("${app.domain}")
    public String domain;
    @Value("${app.subdomain}")
    public String subDomain;
    @Value("${app.language}")
    public String language;
    @Value("${app.defaultSchema}")
    public String defaultSchema;

    private String canonicalSchema;
    private String canonicalLocalSchema;

    @PostConstruct
    private void init() {
        canonicalSchema = getSchemaFromUrisFactory(urisFactoryHost,"/uri-factory/schema",defaultSchema);
        canonicalLocalSchema = getSchemaFromUrisFactory(urisFactoryHost,"/uri-factory/local-schema",defaultSchema);
    }

    @Override
    public URIComponent getURIComponentFromCanonicalURI(String uri) {
        return new URIComponent(canonicalSchema,uri);
    }

    @Override
    public URIComponent getURIComponentFromCanonicalLocalURI(String uri) {
        return new URIComponent(canonicalLocalSchema,uri);
    }

    public String getCanonicalSchema() {
        return canonicalSchema;
    }

    public String getCanonicalLocalSchema() {
        return canonicalLocalSchema;
    }

    private String getSchemaFromUrisFactory(String basePath, String relativePath, String defaultSchema){
        if (basePath.charAt(basePath.length()-1)=='/')
            basePath = basePath.substring(0,basePath.length()-1);
        try {
            URL url = new URL(basePath + relativePath);
            String res = doRequest(url);
            if (res != null && !res.equals("")) {
                return res;
            }
        } catch (Exception e) {

        }

        return defaultSchema;
    }

    @Override
    public JsonObject createCanonicalURIFromResource(TripleObject to, String type, String language, String tripleStore, boolean requestDiscovery) {
        Gson gson = new Gson();
        JsonObject jBody = gson.toJsonTree(to.getAttributes()).getAsJsonObject();
        jBody.addProperty("@class",to.getClassName());
        jBody.addProperty("entityId",to.getId());
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("domain",domain);
        queryParams.put("subDomain",subDomain);
        queryParams.put("type",type);
        queryParams.put("lang",language);
        queryParams.put("requestDiscovery",String.valueOf(requestDiscovery));
        queryParams.put("tripleStore",tripleStore);
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("User-Agent","Mozilla/5.0 ( compatible ) ");
        try {
            JsonObject jResponse = doRequest(new URL(urisFactoryHost+"uri-factory/canonical/resource"), Connection.Method.POST, headers, queryParams, jBody.toString());
            return jResponse;
        } catch (Exception e) {
            System.out.println();
        }
        return null;


    }

    @Override
    public JsonObject createCanonicalURIFromEntity(String className, String canonicalClassName, String type, String language) {
        return null;
    }

    @Override
    public JsonObject createCanonicalURIFromProperty(String className, String canonicalClassName, String type, String language) {
        return null;
    }

    @Override
    public JsonArray linkCanonicalToLocalURI(String canonicalLanguageURI, String localURI, String storageName) {
        return null;
    }

    private String doRequest(URL url) throws IOException {

        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "text/plain");
        con.setDoOutput(true);
        StringBuilder response = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return response.toString();
    }

    private JsonObject doRequest(URL url, Connection.Method method, Map<String,String> headers, Map<String,String> queryParams, String body) throws IOException {

        try {
            OkHttpClient client = new OkHttpClient();
            /* RequestBody b = RequestBody.create(mediaType, "{\r\n    \"@class\": \"es.um.asio.service.util.data.ConceptoGrupo\",\r\n    \"entityId\": 12345,\r\n    \"version\": 0,\r\n    \"idGrupoInvestigacion\": \"E0A6-01\",\r\n    \"numero\": 5,\r\n    \"codTipoConcepto\": \"DESCRIPTORES\"\r\n    }");*/
            Request.Builder rb = new Request.Builder();
            if (headers != null) {
                for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                    rb.addHeader(headerEntry.getKey(), headerEntry.getValue());
                }
            }
            if (queryParams != null) {
                url = buildQueryParams(url, queryParams);
            }
            if (Utils.isValidString(body)) {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody b = RequestBody.create(mediaType, body);
                rb.method(method.toString(), b);
            }
            rb.url(url);
            Request request = rb.build();
            Response response = client.newCall(request).execute();
            return new Gson().fromJson(response.body().string(),JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    private URL buildQueryParams(URL baseURL, Map<String,String> queryParams) throws MalformedURLException, UnsupportedEncodingException {
        StringBuffer base = new StringBuffer();
        base.append(baseURL.toString());
        if (queryParams!=null && queryParams.size()>0) {
            base.append("?");
            List<String> qpList = new ArrayList<>();
            for (Map.Entry<String, String> qpEntry : queryParams.entrySet()) {
                qpList.add(qpEntry.getKey()+"="+ URLEncoder.encode(qpEntry.getValue(), StandardCharsets.UTF_8.toString()));
            }
            base.append(String.join("&",qpList));
        }
        return new URL(base.toString());
    }



}
