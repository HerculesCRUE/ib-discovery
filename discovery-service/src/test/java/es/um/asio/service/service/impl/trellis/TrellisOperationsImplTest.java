package es.um.asio.service.service.impl.trellis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.rdf.TripleObjectLink;
import es.um.asio.service.service.SchemaService;
import es.um.asio.service.service.impl.SchemaServiceImp;
import es.um.asio.service.service.trellis.TrellisCache;
import es.um.asio.service.service.trellis.TrellisCommonOperations;
import es.um.asio.service.service.trellis.TrellisOperations;
import es.um.asio.service.util.TriplesStorageUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.jena.rdf.model.Model;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
public class TrellisOperationsImplTest {

    /**
     * Trellis operations
     */
    @Autowired
    private TrellisOperations trellisOperations;

    /**
     * Trellis operations
     */
    @Autowired
    private SchemaService schemaService;

    @Autowired
    private DataSourcesConfiguration dataSourcesConfiguration;

    private static final String containerName = "test_container";

    /** The trellis url end point. */
    @Value("${app.trellis.endpoint}")
    private String trellisUrlEndPoint;


    @TestConfiguration
    static class TrellisStorageServiceTestConfiguration {
        @Bean
        public TrellisOperations trellisOperations() {
            return new TrellisOperationsImpl();
        }

        @Bean
        public TriplesStorageUtils triplesStorageUtils() {
            return new TriplesStorageUtils();
        }

        @Bean
        public TrellisCommonOperations trellisCommonOperations() {
            return new TrellisCommonOperationsImpl();
        }

        @Bean
        public TrellisCache trellisCache() {
            return new TrellisCacheImpl();
        }

        @Bean
        public SchemaService schemaService() {
            return new SchemaServiceImp();
        }

        @Bean
        public DataSourcesConfiguration dataSourcesConfiguration() {
            return new DataSourcesConfiguration();
        }
    }

    @Before
    public void setUp() {

    }

    @Test
    public void extractPrefix(){
        String mydata = "title";
        Pattern pattern = Pattern.compile("^(.*):");
        Matcher matcher = pattern.matcher(mydata);
        String prefix = null;
        String rest = mydata.replaceFirst("^(.*):","");
        if (matcher.find())
        {
            prefix = matcher.group(1);
        }

        System.out.println();
    }

    @Test
    public void existsContainer() {
        boolean exist = trellisOperations.existsContainer(null,containerName,false);
        if (!exist) {
            String locationContainer =trellisOperations.saveContainer(null,containerName,false);
            Assert.assertNotNull(locationContainer);
        }
        exist = trellisOperations.existsContainer(null,containerName,false);
        Assert.assertTrue(exist);
        if (exist) {
            trellisOperations.deleteContainer(null,containerName,false);
        }
        exist = trellisOperations.existsContainer(null,containerName,false);
        Assert.assertFalse(exist);
    }

    @Test
    public void createContainer() {
        if (trellisOperations.existsContainer(null,containerName,false)) {
            boolean isDeleted = trellisOperations.deleteContainer(null,containerName,false);
            Assert.assertTrue(isDeleted);
        }
        String location = trellisOperations.saveContainer(null,containerName,false);
        if (location!=null) {
            trellisOperations.deleteContainer(null,containerName,false);
        }

        Assert.assertNotNull(location);
    }

    @Test
    public void createContainerInPath() {
/*        if (trellisOperations.existsContainer(null,"lod-links",false)) {
            boolean isDeleted = trellisOperations.deleteContainer(null,"lod-links",false);
            Assert.assertTrue(isDeleted);
        }*/
        String location = trellisOperations.saveContainer(null,"lod-links",false);
        if (location!=null) {
            //trellisOperations.deleteContainer(null,"lod-links",false);
        }

        Assert.assertNotNull(location);
    }

    @Test
    public void deleteContainer() {
        boolean exist = trellisOperations.existsContainer(null,containerName,false);
        if (!exist) {
            trellisOperations.saveContainer(null,containerName,false);
        }
        exist = trellisOperations.existsContainer(null,containerName,false);
        Assert.assertTrue(exist);
        if (exist) {
            trellisOperations.deleteContainer(null,containerName,false);
        }
        exist = trellisOperations.existsContainer(null,containerName,false);
        Assert.assertFalse(exist);
    }

    @Test
    public void getContainer() {
        boolean exist = trellisOperations.existsContainer(null,containerName,false);
        if (!exist) {
            trellisOperations.saveContainer(null,containerName,false);
        }
        Model model = trellisOperations.getContainer(null,containerName,false);
        model.write(System.out);
        Assert.assertTrue(model!=null);

        trellisOperations.deleteContainer(null,containerName,false);
    }

    @Test
    public void createEntry() {
        boolean exist = trellisOperations.existsContainer(null,containerName,false);
        if (!exist) {
            String locationContainer = trellisOperations.saveContainer(null,containerName,false);
            Assert.assertNotNull(locationContainer);
        }

        JsonObject jTripleObjectLink = generateJsonTripleObjectLink();

        TripleObjectLink tol = new TripleObjectLink(jTripleObjectLink);

        // Creo la entidad
        String locationEntity = trellisOperations.saveEntry(containerName,tol,false);
        System.out.println();
        // Borro la entidad
        boolean isDeletedEntity = trellisOperations.deleteEntry(containerName,tol,false);

        // Delete container
        boolean isDeleted = trellisOperations.deleteContainer(null,containerName,false);
        Assert.assertNotNull(locationEntity);
        Assert.assertTrue(isDeletedEntity);
        Assert.assertTrue(isDeleted);
    }

    @Test
    public void addPropertyToEntity() {
        boolean exist = trellisOperations.existsContainer(null,containerName,false);
        if (!exist) {
            String locationContainer = trellisOperations.saveContainer(null,containerName,false);
            Assert.assertNotNull(locationContainer);
        }

        JsonObject jTripleObjectLink = generateJsonTripleObjectLink();

        TripleObjectLink tol = new TripleObjectLink(jTripleObjectLink);

        // Creacion de la entidad
        String  locationEntity = trellisOperations.saveEntry(containerName,tol,false);

        // Añadir las nuevas tripletas
        List<Pair<String,String>> properties = new ArrayList<>();
        properties.add(new MutablePair<>("skos:closeMatch","https://api.elsevier.com/content/article/eid/1-s2.0-S0360544219305369"));

        String entityUpdatedLocation = trellisOperations.addPropertyToEntity(containerName,tol,properties,false);
        // Borro la entidad
        boolean isDeletedEntity = trellisOperations.deleteEntry(containerName,tol,false);

        // Delete container
        boolean isDeleted = trellisOperations.deleteContainer(null,containerName,false);
        Assert.assertNotNull(locationEntity);
        Assert.assertNotNull(entityUpdatedLocation);
        Assert.assertTrue(isDeletedEntity);
        Assert.assertTrue(isDeleted);
    }

    @Test
    public void updateEntry() {
    }

    @Test
    public void deleteEntry() {
    }

    private JsonObject generateJsonTripleObjectLink() {
        String tolStr = "{\n" +
                "    \"id\": \"SCOPUS_ID:85064015825\",\n" +
                "    \"datasetName\": \"SCOPUS\",\n" +
                "    \"baseURL\": \"https://api.elsevier.com/content/\",\n" +
                "    \"remoteName\": \"SCOPUS\",\n" +
                "    \"localClassName\": \"Articulo\",\n" +
                "    \"mapper\": {\n" +
                "      \"dc:title\": \"name\",\n" +
                "      \"doi\": \"doi\"\n" +
                "    },\n" +
                "    \"prefixes\": {\n" +
                "      \"default\": \"http://scopus.com/\",\n" +
                "      \"prism\": \"http://prismstandard.org/namespaces/1.2/basic/\",\n" +
                "      \"dc\": \"http://purl.org/dc/elements/1.1/\",\n" +
                "      \"skos\": \"http://www.w3.org/2004/02/skos/core#\"\n" +
                "    },\n" +
                "    \"links\": [\n" +
                "      {\n" +
                "        \"type\": \"self\",\n" +
                "        \"link\": \"https://api.elsevier.com/content/abstract/scopus_id/85064015825\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"author-affiliation\",\n" +
                "        \"link\": \"https://api.elsevier.com/content/abstract/scopus_id/85064015825?field=author,affiliation\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"scopus\",\n" +
                "        \"link\": \"https://www.scopus.com/inward/record.uri?partnerID=HzOxMe3b&scp=85064015825&origin=inward\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"scopus-citedby\",\n" +
                "        \"link\": \"https://www.scopus.com/inward/citedby.uri?partnerID=HzOxMe3b&scp=85064015825&origin=inward\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"full-text\",\n" +
                "        \"link\": \"https://api.elsevier.com/content/article/eid/1-s2.0-S0360544219305341\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"attributes\": {\n" +
                "      \"@_fa\": \"true\",\n" +
                "      \"prism:url\": \"https://api.elsevier.com/content/abstract/scopus_id/85064015825\",\n" +
                "      \"dc:identifier\": \"SCOPUS_ID:85064015825\",\n" +
                "      \"eid\": \"2-s2.0-85064015825\",\n" +
                "      \"dc:title\": \"Implementation of a new modular facility to detoxify agro-wastewater polluted with neonicotinoid insecticides in farms by solar photocatalysis\",\n" +
                "      \"dc:creator\": \"Fenoll J.\",\n" +
                "      \"prism:publicationName\": \"Energy\",\n" +
                "      \"prism:issn\": \"03605442\",\n" +
                "      \"prism:volume\": \"175\",\n" +
                "      \"prism:pageRange\": \"722-729\",\n" +
                "      \"prism:coverDate\": \"2019-05-15\",\n" +
                "      \"prism:coverDisplayDate\": \"15 May 2019\",\n" +
                "      \"prism:doi\": \"10.1016/j.energy.2019.03.118\",\n" +
                "      \"pii\": \"S0360544219305341\",\n" +
                "      \"citedby-count\": \"8\",\n" +
                "      \"affiliation\": [\n" +
                "        {\n" +
                "          \"@_fa\": \"true\",\n" +
                "          \"affilname\": \"Sustainability and Quality Group of Fruit and Vegetable Products. Murcia Institute of Agri-Food Research and Development\",\n" +
                "          \"affiliation-city\": \"Murcia\",\n" +
                "          \"affiliation-country\": \"Spain\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"@_fa\": \"true\",\n" +
                "          \"affilname\": \"Sustainability and Quality Group of Fruit and Vegetable Products. Universidad de la Vida Institute of Agri-Food Research and Development\",\n" +
                "          \"affiliation-city\": \"Universidad de la vida\",\n" +
                "          \"affiliation-country\": \"Spain\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"prism:aggregationType\": \"Journal\",\n" +
                "      \"subtype\": \"ar\",\n" +
                "      \"subtypeDescription\": \"Article\",\n" +
                "      \"source-id\": \"29348\",\n" +
                "      \"openaccess\": \"0\",\n" +
                "      \"openaccessFlag\": false\n" +
                "    }\n" +
                "  }";
        return new Gson().fromJson(tolStr,JsonObject.class);
    }
}