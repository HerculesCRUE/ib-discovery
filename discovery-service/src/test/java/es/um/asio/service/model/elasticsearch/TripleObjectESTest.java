package es.um.asio.service.model.elasticsearch;

import com.google.gson.internal.LinkedTreeMap;
import data.DataGenerator;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.util.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
class TripleObjectESTest {

    List<TripleObjectES> tosES;
    List<TripleObject> tos;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        tos = dataGenerator.getTripleObjects();
        tosES = new ArrayList<>();
        int i = 0;
        for (TripleObject to : tos) {
            TripleObjectES toES = new TripleObjectES(to);
            toES.setId(i+1);
            if (i!=20) {
                toES.setScore(20f - ((float) i));
            } else {
                toES.setScore(20f - ((float) (i+1)));
            }
            toES.setId(++i);
            tosES.add(toES);
        }
    }

    @Test
    void compareTo() {
        for (int i = 0 ; i < tosES.size()-1 ; i++) {
            Assert.assertTrue(tosES.get(i).compareTo(tosES.get(i+1)) < 0);
        }
    }

    @Test
    void getTripleObjects() {
        List<TripleObject> tosAux = TripleObjectES.getTripleObjects(tosES);
        for (TripleObject to : tos) {
            Assert.assertTrue(tosAux.contains(to));
        }
    }

    @Test
    void getId() {
        for (int i = 0 ; i < tosES.size()-1 ; i++) {
            Assert.assertTrue(tosES.get(i).getId() > 0);
        }
    }

    @Test
    void getEntityId() {
        for (int i = 0 ; i < tosES.size()-1 ; i++) {
            Assert.assertTrue(Utils.isValidString(tosES.get(i).getEntityId()));
        }
    }

    @Test
    void getLocalURI() {
        for (int i = 0 ; i < tosES.size()-1 ; i++) {
            Assert.assertTrue(Utils.isValidString(tosES.get(i).getLocalURI()));
        }
    }

    @Test
    void getClassName() {
        for (int i = 0 ; i < tosES.size()-1 ; i++) {
            Assert.assertTrue(Utils.isValidString(tosES.get(i).getClassName()));
        }
    }

    @Test
    void getLastModification() {
        for (int i = 0 ; i < tosES.size()-1 ; i++) {
            Assert.assertTrue(tosES.get(i).getLastModification() != null);
        }
    }

    @Test
    void getTripleStore() {
        for (TripleObjectES toES : tosES) {
            Assert.assertTrue(toES.getTripleStore()!=null);
        }
    }

    @Test
    void getAttributes() {
        for (TripleObjectES toES : tosES) {
            TripleObject to = new TripleObject(toES);
            Assert.assertTrue(toES.getAttributes().equals(to.getAttributes()));
        }
    }

    @Test
    void getScore() {
        for (TripleObjectES toES : tosES) {
            Assert.assertTrue(toES.getScore()!=0);
        }
    }

    @Test
    void setId() {
        int i = 0;
        for (TripleObjectES toES : tosES) {
            toES.setId(++i);
            Assert.assertTrue(toES.getId() == i);
        }
    }

    @Test
    void setEntityId() {
        int i = 0;
        for (TripleObjectES toES : tosES) {
            toES.setEntityId(String.format("entityId_%s",++i));
            Assert.assertTrue(toES.getEntityId().equals(String.format("entityId_%s",i)));
        }
    }

    @Test
    void setLocalURI() {
        int i = 0;
        for (TripleObjectES toES : tosES) {
            toES.setLocalURI(String.format("http://localhost:8080/%s",++i));
            Assert.assertTrue(toES.getLocalURI().equals(String.format("http://localhost:8080/%s",i)));
        }
    }

    @Test
    void setClassName() {
        int i = 0;
        for (TripleObjectES toES : tosES) {
            toES.setClassName(String.format("clase_%s",++i));
            Assert.assertTrue(toES.getClassName().equals(String.format("clase_%s",i)));
        }
    }

    @Test
    void setLastModification() {
        for (TripleObjectES toES : tosES) {
            Date d = new Date();
            toES.setLastModification(d);
            Assert.assertTrue(toES.getLastModification().equals(d));
        }
    }

    @Test
    void setTripleStore() {
        int i = 0;
        for (TripleObjectES toES : tosES) {
            i++;
            TripleStore ts = new TripleStore(String.format("ts_%s",i),String.format("node_%s",i));
            toES.setTripleStore(ts);
            Assert.assertTrue(toES.getTripleStore().equals(ts));
        }
    }

    @Test
    void setAttributes() {

        for (TripleObjectES toES : tosES) {
            LinkedTreeMap<String,Object> attrs = new LinkedTreeMap<>();
            for (int i = 1 ; i <= 5 ; i++) {
                attrs.put(String.format("att_%s",i),String.format("val_%s",i));
            }
            toES.setAttributes(attrs);
            Assert.assertTrue(toES.getAttributes().equals(attrs));
        }
    }

    @Test
    void setScore() {
        for (TripleObjectES toES : tosES) {
            toES.setScore(1f);
            Assert.assertTrue(toES.getScore() == 1f);
        }
    }

    @Test
    void testEquals() {
        for (TripleObjectES toES : tosES) {
            for (TripleObjectES toESInner : tosES) {
                if (toES.getId() == toESInner.getId())
                    Assert.assertTrue(toES.equals(toESInner));
                else
                    Assert.assertFalse(toES.equals(toESInner));
            }
        }
    }

    @Test
    void canEqual() {
        for (TripleObjectES toES : tosES) {
            for (TripleObjectES toESInner : tosES) {
                if (toES.getId() == toESInner.getId())
                    Assert.assertTrue(toES.equals(toESInner));
                else
                    Assert.assertFalse(toES.equals(toESInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (TripleObjectES toES : tosES) {
            for (TripleObjectES toESInner : tosES) {
                if (toES.getId() == toESInner.getId())
                    Assert.assertTrue(toES.hashCode() == toESInner.hashCode());
                else
                    Assert.assertFalse(toES.hashCode() == toESInner.hashCode());
            }
        }
    }

    @Test
    void testToString() {
        for (TripleObjectES toES : tosES) {
            Assert.assertTrue(Utils.isValidString(toES.toString()));
        }
    }
}