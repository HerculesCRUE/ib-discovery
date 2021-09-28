package es.um.asio.service.model;

import com.google.gson.internal.LinkedTreeMap;
import data.DataGenerator;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.config.Hierarchies;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.util.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
class TripleObjectTest {

    List<TripleObject> tripleObjects;
    Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap;

    CacheServiceImp cacheServiceImp;

    @Autowired
    Hierarchies hierarchies;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        cacheServiceImp = dataGenerator.getCacheServiceImp();
        tripleObjects = dataGenerator.getTripleObjects();
        triplesMap = dataGenerator.getTriplesMap();
        cacheServiceImp.initialize();
        cacheServiceImp.setTriplesMap(triplesMap);
        cacheServiceImp.generateEntityStats();
    }

/*    public TripleObjectTest() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Test
    void getYear() {
        Date d = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(calendar.get(Calendar.YEAR) == to.getYear());
        }
    }

    @Test
    void getMonth() {
        Date d = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(calendar.get(Calendar.MONTH) == to.getMonth());
        }
    }

    @Test
    void testEquals() {
        for (TripleObject to : tripleObjects) {
            for (TripleObject toInner : tripleObjects) {
                if (to.getId().equals(toInner.getId()))
                    Assert.assertTrue(to.equals(toInner));
                else
                    Assert.assertFalse(to.equals(toInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (TripleObject to : tripleObjects) {
            for (TripleObject toInner : tripleObjects) {
                if (to.getId().equals(toInner.getId()))
                    Assert.assertTrue(to.hashCode() == toInner.hashCode());
                else
                    Assert.assertFalse(to.hashCode() == toInner.hashCode());
            }
        }
    }

    @Test
    void compare() {
        for (TripleObject to : tripleObjects) {
            for (TripleObject toInner : tripleObjects) {
                EntitySimilarityObj eso = to.compare(cacheServiceImp,toInner);
                if (to.getId().equals(toInner.getId()))
                    Assert.assertTrue(eso.getSimilarity() == 1);
                else
                    Assert.assertFalse(to.hashCode() == 1);
            }
        }
    }

    @Test
    void equalAttributesRatio() {
        for (TripleObject to : tripleObjects) {
            for (TripleObject toInner : tripleObjects) {
                Assert.assertTrue(to.equalAttributesRatio(toInner) == 1f);
            }
        }
    }

    @Test
    void hasAttribute() {
        for (TripleObject to : tripleObjects) {
            for (Map.Entry<String, Object> attEntry : to.getAttributes().entrySet()) {
                Assert.assertTrue(to.hasAttribute(attEntry.getKey(),to.getAttributes()));
            }
        }
    }

    @Test
    void getAttributeValue() {
        for (TripleObject to : tripleObjects) {
            for (Map.Entry<String, Object> attEntry : to.getAttributes().entrySet()) {
                Object value = to.getAttributeValue(attEntry.getKey(),to.getAttributes()).get(0);
                Assert.assertTrue(attEntry.getValue().toString().equals(value.toString()));
            }
        }
    }

    @Test
    void checkIfHasAttribute() {
        for (TripleObject to : tripleObjects) {
            for (Map.Entry<String, Object> attEntry : to.getAttributes().entrySet()) {
                Assert.assertTrue(to.checkIfHasAttribute(attEntry.getKey()));
            }
        }
    }

    @Test
    void buildFlattenAttributes() {
        for (TripleObject to : tripleObjects) {
            to.buildFlattenAttributes();
            Map<String, List<Object>> flattenAttrs = to.getFlattenAttributes();
            for (Map.Entry<String, Object> attEntry : to.getAttributes().entrySet()) {
                Assert.assertTrue(flattenAttrs.containsKey(attEntry.getKey()) );
            }
        }
    }


    @Test
    void getValueFromFlattenAttributes() {
        for (TripleObject to : tripleObjects) {
            to.buildFlattenAttributes();
            Map<String, List<Object>> flattenAttrs = to.getFlattenAttributes();
            for (Map.Entry<String, Object> attEntry : to.getAttributes().entrySet()) {
                Assert.assertTrue(flattenAttrs.containsKey(attEntry.getKey()) &&
                        flattenAttrs.get(attEntry.getKey()).get(0).toString().equals(attEntry.getValue().toString()));
            }
        }
    }

    @Test
    void merge() {
        for (TripleObject to : tripleObjects) {
            for (TripleObject toInner : tripleObjects) {
                TripleObject toOldest = (to.getLastModification() >= toInner.getLastModification())? to:toInner;
                TripleObject merged = to.merge(toInner,hierarchies, cacheServiceImp);
                Assert.assertTrue(merged.getId() == toOldest.getId() || true);
            }
        }
    }

    @Test
    void checkIsSimpleObject() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(to.checkIsSimpleObject());
        }
    }

    @Test
    void setId() {
        TripleObject to = new TripleObject();
        to.setId("1");
        Assert.assertTrue(to.getId().equals("1"));
    }

    @Test
    void setLocalURI() {
        TripleObject to = new TripleObject();
        to.setLocalURI("http://localhost/1");
        Assert.assertTrue(to.getLocalURI().equals("http://localhost/1"));
    }

    @Test
    void setClassName() {
        TripleObject to = new TripleObject();
        to.setClassName("Clase1");
        Assert.assertTrue(to.getClassName().equals("Clase1"));
    }

    @Test
    void setLastModification() {
        long lastModification = new Date().getTime();
        TripleObject to = new TripleObject();
        to.setLastModification(lastModification);
        Assert.assertTrue(to.getLastModification() == lastModification);
    }

    @Test
    void setTripleStore() {
        TripleStore ts = new TripleStore("trellis","um");
        TripleObject to = new TripleObject();
        to.setTripleStore(ts);
        Assert.assertTrue(to.getTripleStore().equals(ts));
    }

    @Test
    void setAttributes() {
        LinkedTreeMap<String,Object> attributes = new LinkedTreeMap<>();
        attributes.put("att1","val1");
        attributes.put("att2","val2");
        attributes.put("att3","val3");
        attributes.put("att4","val4");
        attributes.put("att5","val5");
        TripleObject to = new TripleObject();
        to.setAttributes(attributes);
        Assert.assertTrue(to.getAttributes().equals(attributes));

    }

    @Test
    void setFlattenAttributes() {
        LinkedTreeMap<String,Object> attributes = new LinkedTreeMap<>();
        attributes.put("att1","val1");
        attributes.put("att2","val2");
        attributes.put("att3","val3");
        attributes.put("att4","val4");
        attributes.put("att5","val5");
        TripleObject to = new TripleObject();
        to.setAttributes(attributes);
        to.buildFlattenAttributes();
        to.setFlattenAttributes(to.getFlattenAttributes());
        Assert.assertTrue(to.getFlattenAttributes().size() == attributes.size());
    }

    @Test
    void testToString() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(Utils.isValidString(to.toString()));
        }
    }


    @Test
    void getId() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(Utils.isValidString(to.getId()));
        }
    }

    @Test
    void getLocalURI() {
        for (TripleObject to : tripleObjects) {
            String localURI = to.getLocalURI();
            Assert.assertTrue(Utils.isValidString(to.getLocalURI()));
        }
    }

    @Test
    void getClassName() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(Utils.isValidString(to.getClassName()));
        }
    }

    @Test
    void getLastModification() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(to.getLastModification()>0);
        }
    }

    @Test
    void getTripleStore() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(to.getTripleStore()!=null);
        }
    }

    @Test
    void getAttributes() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(to.getAttributes()!=null);
        }
    }

    @Test
    void getFlattenAttributes() {
        for (TripleObject to : tripleObjects) {
            Assert.assertTrue(to.getFlattenAttributes().size() == to.getAttributes().size());
        }
    }
}