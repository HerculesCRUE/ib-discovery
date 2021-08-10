package es.um.asio.service.service.impl;

import es.um.asio.service.config.Hierarchies;
import es.um.asio.service.model.TripleObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
/*@SpringBootTest(classes={TestApplication.class})*/
class JobHandlerServiceImpTest {



    /*@Autowired
    DiscoveryApplicationRepository discoveryApplicationRepository;

    @Autowired
    JobRegistryRepository jobRegistryRepository;

    @Autowired
    RequestRegistryRepository requestRegistryRepository;

    @Autowired
    RequestRegistryProxy requestRegistryProxy;

    @Autowired
    ApplicationState applicationState;

    @Autowired
    ObjectResultRepository objectResultRepository;


    @Test
    void testCreateApplication() {
        DiscoveryApplication discoveryApplication = new DiscoveryApplication("appTest");
        discoveryApplication = discoveryApplicationRepository.save(discoveryApplication);
        Assert.assertFalse(discoveryApplicationRepository.findById(discoveryApplication.getId()).isEmpty());
        discoveryApplicationRepository.delete(discoveryApplication);
        Assert.assertTrue(discoveryApplicationRepository.findById(discoveryApplication.getId()).isEmpty());
    }*/


/*    @Test
    void testJobRegistry() {
        DiscoveryApplication discoveryApplication = applicationState.getApplication();
        JobRegistry jobRegistry = jobRegistryRepository.findOpenJobsByDiscoveryAppAndNodeAndTripleStoreAndClassName(discoveryApplication.getId(),"um","trellis","test");
        if (jobRegistry == null) {
            jobRegistry = new JobRegistry(discoveryApplication,"um","trellis","test");
        }
        RequestRegistry requestRegistry1 = new RequestRegistry("u1","r1", RequestType.ENTITY_LINK_CLASS,new Date());
        jobRegistry.addRequestRegistry(requestRegistry1);
        RequestRegistry requestRegistry2 = new RequestRegistry("u1","r2", RequestType.ENTITY_LINK_INSTANCE,new Date());
        jobRegistry.addRequestRegistry(requestRegistry2);
        jobRegistryRepository.save(jobRegistry);
        for (RequestRegistry requestRegistry: jobRegistry.getRequestRegistries()) {
            requestRegistryRepository.save(requestRegistry);
        }
        JobRegistry jobRegistry2 = jobRegistryRepository.findOpenJobsByDiscoveryAppAndNodeAndTripleStoreAndClassName(discoveryApplication.getId(),"um","trellis","test");
        System.out.println();
    }*/

/*    @Test void testSimilarities() throws Exception {
        DiscoveryApplication discoveryApplication = applicationState.getApplication();
        JobRegistry jobRegistry = jobRegistryRepository.findOpenJobsByDiscoveryAppAndNodeAndTripleStoreAndClassName(discoveryApplication.getId(),"um","trellis","test");
        if (jobRegistry == null) {
            jobRegistry = new JobRegistry(discoveryApplication,"um","trellis","test",false);
        }
        RequestRegistry requestRegistry;
        Optional<RequestRegistry> requestRegistryOpt = requestRegistryRepository.findByUserIdAndRequestCodeAndRequestType("u1","r1", RequestType.ENTITY_LINK_CLASS);
        if (requestRegistryOpt.isEmpty()) {
            requestRegistry = new RequestRegistry("u1","r1", RequestType.ENTITY_LINK_CLASS,new Date());
        } else {
            requestRegistry = requestRegistryOpt.get();
        }
        jobRegistry.addRequestRegistry(requestRegistry);
        jobRegistryRepository.save(jobRegistry);

        // Creation of TO (String node, String tripleStore, String className, JSONObject jData )
        JSONObject jData = new JSONObject();
        JSONObject jDataInner = new JSONObject();
        jDataInner.put("año","2012");
        jDataInner.put("mes","10");
        jDataInner.put("dia","22");
        jData.put("descripcion","CONTRATACIÓN LABORAL DE DOCTORES RECIÉN TITULADOS EN ORGANISMOS DE INVESTIGACIÓN");
        //jData.put("fechaConvocatoria","2012/10/22 00:00:00");
        jData.put("fechaConvocatoria",jDataInner);
        jData.put("fechaPublicacionBoletin","2012/10/22 00:00:00");
        jData.put("idConvocatoriaRecursoHumano","1244");
        JSONArray jArray = new JSONArray();
        jArray.put("11411");
        jArray.put("11412");
        JSONObject jDataInArray = new JSONObject();
        jDataInArray.put("p1","p1");
        jDataInArray.put("p2","p2");
        jDataInArray.put("p3","p3");
        jDataInArray.put("p4","p4");
        jArray.put(jDataInArray);
        jData.put("idEmpresaFinanciadora",jArray);
        TripleObject to1 = new TripleObject("um","trellis","ConvocatoriaRecursosHumanos",jData);
        to1.setId("12345");
        ObjectResult or = new ObjectResult(jobRegistry,to1,0.99f);
        TripleObject to2 = or.toTripleObject(jobRegistry);
        // objectResultRepository.save(or);
        TripleObject t3 = to2.merge(to1);
        System.out.println();
    }*/

    @Autowired
    Hierarchies hierarchies;

    @Test void testMerge() throws Exception {
        String data1 = "{\n" +
                "  \"description\": \"CONTRATACIÓN LABORAL DE DOCTORES RECIÉN TITULADOS EN ORGANISMOS DE INVESTIGACIÓN\",\n" +
                "  \"nombre\": \"Proyecto 1\",\n" +
                "  \"fechaInicio\":\"2012/01/01 01:01:01\",\n" +
                "  \"fechaFin\":\"2020/01/01 01:01:01\",\n" +
                "  \"gruposDeInvestigacion\": [\n" +
                "    {\n" +
                "      \"id\":1,\n" +
                "      \"nombre\": \"grupo 1\",\n" +
                "      \"miembros\": [\n" +
                "        {\n" +
                "          \"id\":\"1\",\n" +
                "          \"nombre\": \"Persona 1\",\n" +
                "          \"apellidos\": \"Apellidos 1\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\":\"2\",\n" +
                "          \"nombre\": \"Persona 2\",\n" +
                "          \"apellidos\": \"Apellidos 1\"\n" +
                "        }\n" +
                "        \n" +
                "      ]\n" +
                "    },\n" +
                "        {\n" +
                "      \"id\":2,\n" +
                "      \"nombre\": \"grupo 2\",\n" +
                "      \"miembros\": [\n" +
                "        {\n" +
                "          \"id\":\"3\",\n" +
                "          \"nombre\": \"Persona 3\",\n" +
                "          \"apellidos\": \"Apellidos 3\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\":\"4\",\n" +
                "          \"nombre\": \"Persona 4\",\n" +
                "          \"apellidos\": \"Apellidos 4\"\n" +
                "        }\n" +
                "        \n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String data2 = "{\n" +
                "  \"description\": \"CONTRATACIÓN LABORAL DE DOCTORES RECIÉN TITULADOS EN ORGANISMOS DE INVESTIGACIÓN\",\n" +
                "  \"nombre\": \"Proyecto 1\",\n" +
                "  \"fechaInicio\":\"2012/01/01 01:01:01\",\n" +
                "  \"fechaFin\":\"2020/01/01 01:01:01\",\n" +
                "  \"gruposDeInvestigacion\": [\n" +
                "    {\n" +
                "      \"id\":1,\n" +
                "      \"nombre\": \"grupo 1\",\n" +
                "      \"miembros\": [\n" +
                "        {\n" +
                "          \"id\":\"1\",\n" +
                "          \"nombre\": \"Persona 1\",\n" +
                "          \"apellidos\": \"Apellidos 1\"\n" +
                "        }\n" +
                "        \n" +
                "      ]\n" +
                "    },\n" +
                "        {\n" +
                "      \"id\":2,\n" +
                "      \"nombre\": \"grupo 2\",\n" +
                "      \"miembros\": [\n" +
                "        {\n" +
                "          \"id\":\"3\",\n" +
                "          \"nombre\": \"Persona 3\",\n" +
                "          \"apellidos\": \"Apellidos 3\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONObject jData1 = new JSONObject(data1);
        JSONObject jData2 = new JSONObject(data2);
        TripleObject to1 = new TripleObject("um","trellis","Proyecto",jData1);
        to1.setId("1");
        to1.setLastModification(new Date().getTime()-10000);
        TripleObject to2 = new TripleObject("um","trellis","Proyecto",jData2);
        to2.setId("2");
        to2.setLastModification(new Date().getTime());
        TripleObject to3 = to1.merge(to2,hierarchies);
        Assert.assertTrue(to3.getId() == to2.getId() || to3.getId() == to1.getId() );
    }

}