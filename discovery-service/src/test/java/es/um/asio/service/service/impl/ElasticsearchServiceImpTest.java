package es.um.asio.service.service.impl;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
/*@SpringBootTest(classes={TestApplication.class})*/
class ElasticsearchServiceImpTest {

    /*@Autowired
    ElasticsearchServiceImp esService;

    static Set<TripleObjectES> tripleObjectsES;

    @BeforeEach
    void setUp() {
        tripleObjectsES = new HashSet<>();

        for (int i = 0 ; i < 10 ; i++) {
            TripleObjectES to = new TripleObjectES(String.valueOf(i),"um","trellis",String.format("clase1"), new Date());
            to.setTripleStore(new TripleStore("trellis","um","http://herc-iz-front-desa.atica.um.es/","admin","admin"));
            LinkedTreeMap<String,Object> attrs = new LinkedTreeMap<>();
            for (int j = 1 ; j <= 5 ; j++){
                attrs.put(String.format("@key%s",j),String.format("@val%s_%s",i,j));
            }
            to.setAttributes(attrs);
            tripleObjectsES.add(to);
        }
    }

    @AfterEach
    void setDown() {
        esService.deleteTripleObjectsES(new ArrayList<>(tripleObjectsES));
    }


    @Test
    @Order(1)
    void saveTripleObjectES() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        String res = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertTrue(res.equals("inserted"));
        esService.deleteTripleObjectES(tripleObjectES);
        Assert.assertNull(esService.getTripleObjectESById(tripleObjectES.getEntityId()));
    }

    @Test
    @Order(2)
    void saveTripleObjectsES() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);
        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjectsES(toESs);
        Assert.assertTrue(toESs.size() == rToESs.size());
        esService.deleteTripleObjectsES(rToESs);
        List<TripleObjectES> r = esService.getTripleObjectsESByFilterAndAttributes("triple-object", "um", "trellis","clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);

    }

    @Test
    @Order(3)
    void saveTripleObject() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObject tripleObject = new TripleObject(tripleObjectES);
        String res = esService.saveTripleObject(tripleObject);
        Assert.assertTrue(res.equals("inserted"));
        esService.deleteTripleObjectES(tripleObjectES);
        Assert.assertNull(esService.getTripleObjectESById(tripleObjectES.getEntityId()));
    }

    @Test
    @Order(4)
    void saveTripleObjects() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);

        List<TripleObject> tos = new ArrayList<>();
        for (TripleObjectES toES : toESs) {
            tos.add(new TripleObject(toES));
        }

        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjects(tos);
        Assert.assertTrue(tos.size() == rToESs.size());
        esService.deleteTripleObjectsES(rToESs);
        List<TripleObjectES> r = esService.getTripleObjectsESByFilterAndAttributes("triple-object", "um", "trellis","clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);
    }

    @Test
    @Order(5)
    void deleteTripleObjectES() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        String res = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertTrue(res.equals("inserted"));
        esService.deleteTripleObjectES(tripleObjectES);
        Assert.assertNull(esService.getTripleObjectESById(tripleObjectES.getEntityId()));
    }

    @Test
    @Order(6)
    void deleteTripleObjectsES() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);
        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjectsES(toESs);
        Assert.assertTrue(toESs.size() == rToESs.size());
        esService.deleteTripleObjectsES(rToESs);
        List<TripleObjectES> r = esService.getTripleObjectsESByFilterAndAttributes("triple-object", "um", "trellis", "clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);
    }

    @Test
    @Order(7)
    void deleteTripleObject() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        TripleObject tripleObject = new TripleObject(tripleObjectES);
        String res = esService.saveTripleObject(tripleObject);
        Assert.assertTrue(res.equals("inserted"));
        esService.deleteTripleObject(tripleObject);
        Assert.assertNull(esService.getTripleObjectESById(tripleObjectES.getEntityId()));
    }

    @Test
    @Order(8)
    void deleteTripleObjects() {
        List<TripleObjectES> toESs = new ArrayList<>();
        toESs.addAll(tripleObjectsES);

        List<TripleObject> tos = new ArrayList<>();
        for (TripleObjectES toES : toESs) {
            tos.add(new TripleObject(toES));
        }

        List<TripleObjectES> rToESs = (List<TripleObjectES>) esService.saveTripleObjects(tos);
        Assert.assertTrue(tos.size() == rToESs.size());
        esService.deleteTripleObjects(tos);
        List<TripleObjectES> r = esService.getTripleObjectsESByFilterAndAttributes("triple-object", "um", "trellis", "clase1",new ArrayList<Pair<String, Object>>());
        Assert.assertTrue(r.size() == 0);
    }

    @Test
    @Order(9)
    void getAll() {
        List<TripleObjectES> a = esService.getAll();
        esService.saveTripleObjectsES(new ArrayList<>(tripleObjectsES));
        List<TripleObjectES> res = esService.getAll();
        Assert.assertTrue(res.size() >= tripleObjectsES.size());
        esService.deleteTripleObjectsES(new ArrayList<>(tripleObjectsES));
    }

    @Test
    @Order(10)
    void getAllMappedById() {
        List<TripleObjectES> a = esService.getAll();
        esService.saveTripleObjectsES(new ArrayList<>(tripleObjectsES));
        Map<String, TripleObjectES> x = esService.getAllMappedById();
        Assert.assertTrue(esService.getAllMappedById().size() >= tripleObjectsES.size());
        esService.deleteTripleObjectsES(new ArrayList<>(tripleObjectsES));
    }

    @Test
    @Order(11)
    void getTripleObjectESById() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        String res = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertTrue(res.equals("inserted"));
        esService.deleteTripleObjectES(tripleObjectES);
        Assert.assertNull(esService.getTripleObjectESById(tripleObjectES.getEntityId()));
    }

    @Test
    @Order(12)
    void getTripleObjectById() {
        TripleObjectES tripleObjectES = (TripleObjectES) tripleObjectsES.toArray()[0];
        String res = esService.saveTripleObjectES(tripleObjectES);
        Assert.assertNotNull(esService.getTripleObjectESById(tripleObjectES.getEntityId()));
        Assert.assertTrue(res.equals("inserted"));
    }

    @Test
    @Order(13)
    void getTripleObjectsESByClassNameAndAttributes() {
        for (TripleObjectES toES : tripleObjectsES) {
            List<Pair<String, Object>> params = new ArrayList<>();
            for (Map.Entry<String, Object> p : toES.getAttributes().entrySet()) {
                params.add(new Pair<>(p.getKey(),p.getKey()));
            }
            Assert.assertNotNull(esService.getTripleObjectsESByFilterAndAttributes("triple-object",  "um", "trellis","clase1", params));
        }
    }

    @Test
    @Order(14)
    void getTripleObjectsByClassNameAndAttributes() {
        for (TripleObjectES toES : tripleObjectsES) {
            List<Pair<String, Object>> params = new ArrayList<>();
            for (Map.Entry<String, Object> p : toES.getAttributes().entrySet()) {
                params.add(new Pair<>(p.getKey(),p.getKey()));
            }
            Assert.assertNotNull(esService.getTripleObjectsByFilterAndAttributes("triple-object",  "um", "trellis","clase1", params));
        }
    }

    @Test
    @Order(15)
    void getSimilarTripleObjectsES() {
        List<Pair<String, Object>> params = new ArrayList<>();
        params.add(new Pair<>("idPersona","9920"));
        params.add(new Pair<>("depNombre","SERVICIO GESTION INVESTIGACION"));
        List<TripleObject> res = esService.getTripleObjectsByFilterAndAttributes("triple-object",  "um", "trellis", "Persona", params);
    }

    @Test
    @Order(16)
    void getSimilarTripleObjects() {
    }

    @Test
    @Order(16)
    void postProjectTripleObjectES() {

        TripleStore ts = new TripleStore("trellis", "um-2", "http://herc-iz-front-desa.atica.um.es/", "admin", "admin");
        JsonObject jData = new JsonObject();
        jData.add("proyecto.cerrado",new JsonObject());
        jData.get("proyecto.cerrado").getAsJsonObject().addProperty("@language","es");
        jData.get("proyecto.cerrado").getAsJsonObject().addProperty("@val","2011/12/31 00:00:00");
        jData.add("proyecto.descripcion",new JsonObject());
        jData.get("proyecto.descripcion").getAsJsonObject().addProperty("@language","es");
        jData.get("proyecto.descripcion").getAsJsonObject().addProperty("@val","PEPLAN RECURSOS FITOGENÉTICOS S5-E005-12 LIBRO ROJO DE LA FLORA NO VASCULAR AMENAZADA");
        jData.add("proyecto.id",new JsonObject());
        jData.get("proyecto.id").getAsJsonObject().addProperty("@language","es");
        jData.get("proyecto.id").getAsJsonObject().addProperty("@val","12140");
        jData.add("proyecto.nombre",new JsonObject());
        jData.get("proyecto.nombre").getAsJsonObject().addProperty("@language","es");
        jData.get("proyecto.nombre").getAsJsonObject().addProperty("@val","PEPLAN: RECURSOS FITOGENÉTICOS: S5-E005-12: LIBRO ROJO DE LA FLORA NO VASCULAR AMENAZADA.");
        jData.add("proyecto.tipo",new JsonObject());
        jData.get("proyecto.tipo").getAsJsonObject().addProperty("@language","es");
        jData.get("proyecto.tipo").getAsJsonObject().addProperty("@val","AYUDA");
        TripleObject to = new TripleObject(ts, jData, "Proyecto","http:_hercules.org_um_es-ES_rec_Proyecto_12140","http:_hercules.org_um_es-ES_rec_Proyecto_12140", "Fri, 15 May 2020 13:34:23 GMT");
        TripleObjectES toES = new TripleObjectES(to);
        String res = esService.saveTripleObjectES(new TripleObjectES(to));
        Assert.assertTrue(res.equals("inserted"));
        List<TripleObjectES> res2 = esService.getAllByNodeAndTripleStoreAndClassName("um-2","trellis","Proyecto");
        esService.deleteTripleObjectES(new TripleObjectES(to));
        Assert.assertNull(esService.getTripleObjectESById(toES.getEntityId()));
    }

    @Test
    @Order(17)
    void getAllClassAndId() {
        Map<String, Set<String>> response = esService.getAllSimplifiedTripleObject("um","trellis");
        Assert.assertNull(response);
    }*/
}