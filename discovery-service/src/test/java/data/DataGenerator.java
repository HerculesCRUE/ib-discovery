package data;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import lombok.Getter;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.*;

public final class DataGenerator {

    private static List<TripleObject> tripleObjects;
    private static Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap;

    public DataGenerator() throws Exception {
        tripleObjects = new ArrayList<>();
        triplesMap = new HashMap<>();
        JSONObject jData = new JSONObject();
        TripleStore ts = new TripleStore("trellis","um");
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                jData.put(String.format("att-%s", j), String.format("value-%s", j));
            }
            TripleObject to = new TripleObject("um","trellis","class1",jData);
            to.setLastModification(new Date().getTime() - (i*10000));
            to.setId(String.valueOf(i));
            to.setLocalURI("http://localhost/"+i);
            to.buildFlattenAttributes();
            tripleObjects.add(to);
        }

        for (TripleObject to : tripleObjects) {
            if (!triplesMap.containsKey(to.getTripleStore().getNode().getNode()))
                triplesMap.put(to.getTripleStore().getNode().getNode(), new HashMap<>());
            if (!triplesMap.get(to.getTripleStore().getNode().getNode()).containsKey(to.getTripleStore().getTripleStore()))
                triplesMap.get(to.getTripleStore().getNode().getNode()).put(to.getTripleStore().getTripleStore(), new HashMap<>());
            if (!triplesMap.get(to.getTripleStore().getNode().getNode()).get(to.getTripleStore().getTripleStore()).containsKey(to.getClassName()))
                triplesMap.get(to.getTripleStore().getNode().getNode()).get(to.getTripleStore().getTripleStore()).put(to.getClassName(), new HashMap<>());
            triplesMap.get(to.getTripleStore().getNode().getNode()).get(to.getTripleStore().getTripleStore()).get(to.getClassName()).put(to.getId(),to);
        }
    }

    public static List<TripleObject> getTripleObjects() {
        return tripleObjects;
    }

    public static Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        return triplesMap;
    }
}
