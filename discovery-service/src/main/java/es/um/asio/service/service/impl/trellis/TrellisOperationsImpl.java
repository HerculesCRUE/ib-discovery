package es.um.asio.service.service.impl.trellis;

import com.jayway.restassured.response.Response;
import es.um.asio.service.constants.Constants;
import es.um.asio.service.model.rdf.MediaTypes;
import es.um.asio.service.model.rdf.RdfObjectMapper;
import es.um.asio.service.model.rdf.TripleObjectLink;
import es.um.asio.service.service.SchemaService;
import es.um.asio.service.service.impl.SchemaServiceImp;
import es.um.asio.service.service.trellis.TrellisCache;
import es.um.asio.service.service.trellis.TrellisCommonOperations;
import es.um.asio.service.service.trellis.TrellisOperations;
import es.um.asio.service.util.TriplesStorageUtils;
import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TrellisOperationsImpl implements TrellisOperations {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(TrellisOperationsImpl.class);

    /** The trellis url end point. */
    @Value("${app.trellis.endpoint}")
    private String trellisUrlEndPoint;

    @Autowired
    private TrellisCommonOperations trellisCommonOperations;

    /** The trellis utils. */
    @Autowired
    private TriplesStorageUtils triplesStorageUtils;

    @Autowired
    private TrellisCache trellisCache;

    @Autowired
    private SchemaService schemaService;

    @Override
    public boolean existsContainer(String path, String containerName, boolean isFullURI) {
        String url;
        if (isFullURI)
            url = path;
        else
            url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[] {path,containerName}));
        Boolean result = (Boolean) trellisCache.find(url, Constants.CACHE_TRELLIS_CONTAINER);
        if (result == null || !result) { // Si no existe en la cache
            Model model;
            model = trellisCommonOperations.createRequestSpecification()
                    .header("Accept", MediaTypes.TEXT_TURTLE)
                    .expect()
                    .when().get(url)
                    .as(Model.class, new RdfObjectMapper(url));
            if (model == null) {
                result = false;
            } else
                result = model.size() > 0;
        }
        return result;
    }

    @Override
    public String saveContainer(String path, String containerName, boolean isFullURI) {
        logger.info("Creating a container");
        String uri = buildURI(trellisUrlEndPoint,Arrays.asList(new String[]{path,containerName}));
        Model model = ModelFactory.createDefaultModel();
        // model.createProperty("http://hercules.org");
        Resource resourceProperties = model.createResource(uri);
        Property a = model.createProperty("http://www.w3.org/ns/ldp#", "a");
        Property dcterms = model.createProperty("http://purl.org/dc/terms/", "title");
        resourceProperties.addProperty(dcterms, containerName.concat(" Container"));

        Response response;
        try {
            String url;
            if (isFullURI)
                url = path;
            else
                url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[]{path}));

            Model containerModel = getContainer(path,containerName, isFullURI);
            boolean doInsert = false;
            if (containerModel == null) {
                doInsert = true;
                response = trellisCommonOperations.createRequestSpecification()
                        .contentType(MediaTypes.TEXT_TURTLE)
                        .header("slug", containerName)
                        .header("link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                        .body(model, new RdfObjectMapper())
                        .post(url);
            } else {
                url = buildURI(url,Arrays.asList(new String[]{containerName}));
                response = trellisCommonOperations.createRequestSpecification()
                        .contentType(MediaTypes.TEXT_TURTLE)
                        .header("link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                        .body(model, new RdfObjectMapper())
                        .put(url);
            }

            if (response.getStatusCode() != HttpStatus.SC_CREATED && response.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                logger.warn("Can´t create container: {}", response.getStatusCode());
                return null;
            } else {
                logger.info("GRAYLOG-TS Creado contenedor en el path {} de tipo: {}", path, containerName);
                String location;
                if (response.getHeaders().hasHeaderWithName("Location")) {
                    location = response.getHeaders().getValue("Location");
                } else if (response.getHeaders().hasHeaderWithName("Content-Location")) {
                    location = response.getHeaders().getValue("Content-Location");
                } else {
                    if (doInsert) {
                        location = buildURI(url,Arrays.asList(new String[]{containerName}));
                    } else {
                        location = url;
                    }
                }
                return location;
            }
        } catch (Exception e) {
            logger.error("createContainer:" , e);
            return null;
        }

    }

    @Override
    public Model getContainer(String path, String containerName, boolean isFullURI) {
        String url;
        if (isFullURI)
            url = path;
        else
            url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[] {path,containerName}));
        Model model;
        try {
            model = trellisCommonOperations.createRequestSpecification()
                    .header("Accept", MediaTypes.TEXT_TURTLE)
                    .expect()
                    .when()
                    .get(url)
                    .as(Model.class, new RdfObjectMapper(url));
            return model;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteContainer(String path, String containerName, boolean isFullURI) {
        logger.info("Creating a container");

        Response deleteResponse;
        try {
            String url;
            if (isFullURI)
                url = path;
            else
                url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[] {path,containerName}));
            deleteResponse = trellisCommonOperations.createRequestSpecification()
                    .contentType(MediaTypes.TEXT_TURTLE)
                    .delete(url);

            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                logger.warn("The container not exists: {}", deleteResponse.getStatusCode());
                return false;
            } else {
                logger.info("GRAYLOG-TS Borrado el contenedor en el path {} de tipo: {}", path, containerName);
                return true;
            }
        } catch (Exception e) {
            logger.error("createContainer:" , e);
            return false;
        }
    }

    @Override
    public String saveEntry(String path, TripleObjectLink tripleObjectLink, boolean isFullURI) {
        logger.info("Creating a Entity");


        Model model = tripleObjectLink.generateModelFromJson((SchemaServiceImp) schemaService);

        System.out.println("**************************");
        model.write(System.out,MediaTypes.TEXT_TURTLE);
        System.out.println("**************************");
        Response response;
        try {
            String url;
            if (isFullURI)
                url = path;
            else
                url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[] {path}));

            Model entityModel = getEntry(path,tripleObjectLink.getId(),isFullURI);
            String operation = "INSERT";
            boolean doInsert = false;
            if (entityModel!=null) {
                operation = "UPDATE";

                if (isFullURI) {
                    url = url;
                } else {
                    if (url.endsWith("/")) {
                        url = url + tripleObjectLink.getId();
                    } else {
                        url = url + "/" + tripleObjectLink.getId();
                    }
                }

                response = trellisCommonOperations.createRequestSpecification()
                        .contentType(MediaTypes.TEXT_TURTLE)
                        .header("slug", tripleObjectLink.getId())
                        .header("Content-type", MediaTypes.TEXT_TURTLE)
                        .body(model, new RdfObjectMapper())
                        .put(url);
            } else {
                doInsert = true;
                response = trellisCommonOperations.createRequestSpecification()
                        .contentType(MediaTypes.TEXT_TURTLE)
                        .header("slug", tripleObjectLink.getId())
                        .header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"")
                        .header("Content-type", MediaTypes.TEXT_TURTLE)
                        .body(model, new RdfObjectMapper())
                        .post(url);
            }

            if (response.getStatusCode() != HttpStatus.SC_CREATED && response.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                logger.warn("Warn: responseBody: {}", response.getBody().asString());
                logger.warn("Warn: saving the object: {}", model);
                logger.warn("Operation: {}", operation);
                logger.warn("cause: {}", response.getBody().asString());
                logger.warn("Warn: saving in Trellis the object: {}",model);
                return null;

            } else {
                logger.info("GRAYLOG-TS Creado recurso en trellis de tipo: {}", tripleObjectLink.getLocalClassName());
                String location;
                if (response.getHeaders().hasHeaderWithName("Location")) {
                    location = response.getHeaders().getValue("Location");
                } else if (response.getHeaders().hasHeaderWithName("Content-Location")) {
                    location = response.getHeaders().getValue("Content-Location");
                } else {
                    if (doInsert) {
                        location = buildURI(url, Arrays.asList(new String[]{tripleObjectLink.getId()}));
                    } else {
                        location = url;
                    }
                }
                return location;
            }
        } catch (Exception e) {
            logger.error("createContainer:" , e);
            return null;
        }
    }


    @Override
    public boolean deleteEntry(String path, TripleObjectLink tripleObjectLink, boolean isFullURI) {
        String url;
        if (isFullURI)
            url = path;
        else
            url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[] {path,tripleObjectLink.getId()}));
        Response response = trellisCommonOperations.createRequestSpecification()
                .header("Accept", MediaTypes.TEXT_TURTLE)
                .expect()
                .when()
                .delete(url);
        if (response.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            logger.warn("Warn: deleteFail: Status code: {}", response.getStatusCode());
            return false;
        } else {
            logger.info("GRAYLOG-TS Borrada la entidad en trellis de con id : {}", tripleObjectLink.getId());
            return true;
        }
    }


    @Override
    public Model getEntry(String path, String entityId, boolean isFullURI) {

        String url;
        if (isFullURI)
            url = path;
        else
            url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[] {path,entityId}));
        Model model;
        model = trellisCommonOperations.createRequestSpecification()
                .header("Accept", MediaTypes.TEXT_TURTLE)
                .expect()
                .when()
                .get(url)
                .as(Model.class, new RdfObjectMapper(url));
        return model;
    }

    @Override
    public String addPropertyToEntity(String path, TripleObjectLink tripleObjectLink, List<Pair<String, String>> properties, boolean isFullURI) {
        logger.info("addPropertyToEntity");

        for (Pair<String, String> property : properties) {
            tripleObjectLink.getAttributes().put(property.getLeft(),property.getRight());
        }

        Model model = tripleObjectLink.generateModelFromJson((SchemaServiceImp) schemaService);

        System.out.println("**************************");
        model.write(System.out,MediaTypes.TEXT_TURTLE);
        System.out.println("**************************");
        Response response;
        try {
            String url;
            if (isFullURI)
                url = path;
            else
                url = buildURI(trellisUrlEndPoint, Arrays.asList(new String[] {path}));
            Model entityModel;
            try {
                entityModel = getEntry(path, tripleObjectLink.getURLEndedID(), isFullURI);
            } catch (Exception e) {
                entityModel = null;
            }
            String operation = "INSERT";
            if (entityModel!=null) {
                operation = "UPDATE";
            } else {
                operation = "INSERT";
            }

            if (isFullURI) {
                url = url;
            } else {
                if (url.endsWith("/")) {
                    url = url + tripleObjectLink.getURLEndedID(); // AQUI
                } else {
                    url = url + "/" + tripleObjectLink.getURLEndedID();
                }
            }
            response = trellisCommonOperations.createRequestSpecification()
                    .contentType(MediaTypes.TEXT_TURTLE)
/*                        .header("slug", tripleObjectLink.getId())*/
                    .header("Content-type", MediaTypes.TEXT_TURTLE)
                    .body(model, new RdfObjectMapper())
                    .put(url);

            if (response.getStatusCode() != HttpStatus.SC_CREATED && response.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                logger.warn("Warn: responseBody: {}", response.getBody().asString());
                logger.warn("Warn: saving the object: {}", model);
                logger.warn("Operation: {}", operation);
                logger.warn("cause: {}", response.getBody().asString());
                logger.warn("Warn: saving in Trellis the object: {}",model);
                return null;

            } else {
                logger.info("GRAYLOG-TS Creado recurso en trellis de tipo: {}", tripleObjectLink.getLocalClassName());
                String location;
                if (response.getHeaders().hasHeaderWithName("Location")) {
                    location = response.getHeaders().getValue("Location");
                } else if (response.getHeaders().hasHeaderWithName("Content-Location")) {
                    location = response.getHeaders().getValue("Content-Location");
                } else {
                    location = url;
                }
                return location;
            }
        } catch (Exception e) {
            logger.error("createContainer:" , e);
            return null;
        }
    }

    private String buildURI(String baseURL, List<String> uriParts) {
        List<String> uriChunks = new ArrayList<>();
        if (Utils.isValidString(baseURL)) {
            if (baseURL.endsWith("/"))
                uriChunks.add(baseURL.substring(0, baseURL.length()-1));
            else
                uriChunks.add(baseURL);
        }

        for (String part : uriParts) {
            if (Utils.isValidString(part)) {
                if (part.startsWith("/"))
                    part = part.substring(1);
                if (part.endsWith("/"))
                    part = part.substring(0,part.length()-1);
                uriChunks.add(part);
            }
        }
        return String.join("/",uriChunks)+"/";
    }
}
