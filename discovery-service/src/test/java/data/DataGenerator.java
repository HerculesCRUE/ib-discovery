package data;

import es.um.asio.service.model.Node;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.model.relational.*;
import lombok.Getter;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.*;

public final class DataGenerator {

    private static List<TripleObject> tripleObjects;
    private static Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap;
    private static List<Node> nodes;
    private static JobRegistry jobRegistry;

    public DataGenerator() throws Exception {
        tripleObjects = new ArrayList<>();
        triplesMap = new HashMap<>();
        nodes = new ArrayList<>();
        JSONObject jData = new JSONObject();
        TripleStore ts = new TripleStore("trellis","um");
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                jData.put(String.format("att-%s", j), String.format("value-%s", j));
            }
            TripleObject to = new TripleObject("um","trellis","class1",jData);
            Node node = new Node(String.format("node-%s",i));
            to.setLastModification(new Date().getTime() - (i*10000));
            to.setId(String.valueOf(i));
            to.setLocalURI("http://localhost/"+i);
            to.buildFlattenAttributes();
            tripleObjects.add(to);
            nodes.add(node);
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

        // Job registry
        DiscoveryApplication da = new DiscoveryApplication("app1");
        jobRegistry = new JobRegistry(da,"node1","tripeStore1","className1",true);
        jobRegistry.setStatusResult(StatusResult.COMPLETED);
        for (int i = 1 ; i <= 5 ; i++) {
            RequestRegistry rr = new RequestRegistry(
                    String.format("user_%s",i),
                    String.format("requestCode_%s",i),
                    (i%2==1)?RequestType.ENTITY_LINK_CLASS:RequestType.ENTITY_LINK_INSTANCE,
                    new Date()
            );
            jobRegistry.addRequestRegistry(rr);
        }
        // Añado todos los Triple Objects como Object Results
        int actionCounter = 0;
        for (TripleObject to :tripleObjects) {
            ObjectResult or = new ObjectResult(jobRegistry,to,0f);
            or.setId(Long.valueOf(to.getId()));
            jobRegistry.getObjectResults().add(or);
            float i = 0f;
            for (TripleObject toInner :tripleObjects) {
                i++;
                if (Math.abs(Integer.valueOf(to.getId()) - Integer.valueOf(toInner.getId())) <= 1) {
                    ObjectResult orInner = new ObjectResult(null,toInner,1f);
                    orInner.setId(Long.valueOf(toInner.getId()));
                    orInner.setParentAutomatic(or);
                    or.addAutomatic(orInner);
                } else if (Math.abs(Integer.valueOf(to.getId()) - Integer.valueOf(toInner.getId())) <= 2) {
                    ObjectResult orInner = new ObjectResult(null,toInner,0.9f -(i/10f));
                    orInner.setId(Long.valueOf(toInner.getId()));
                    orInner.setParentManual(or);
                    or.addManual(orInner);
                } else {
                    ObjectResult orInner = new ObjectResult(null,toInner,0.9f -(i/10f));
                    orInner.setId(Long.valueOf(toInner.getId()));
                    orInner.setParentLink(or);
                    or.getLink().add(orInner);
                }
            }
            Set<ActionResult> actionResults = new HashSet<>();
            ActionResult arAutoDelete = new ActionResult(Action.DELETE,or);
            ActionResult arAutoUpdate = new ActionResult(Action.UPDATE,or);
            arAutoDelete.setId(++actionCounter);
            arAutoUpdate.setId(++actionCounter);
            for (ObjectResult orAuto : or.getAutomatic()) {
                if (orAuto.getId() == or.getId())
                    arAutoUpdate.addObjectResult(orAuto);
                else
                    arAutoDelete.addObjectResult(orAuto);
            }
            actionResults.add(arAutoUpdate);
            actionResults.add(arAutoDelete);

            ActionResult arLink = new ActionResult(Action.LINK,or);
            arLink.setId(++actionCounter);
            for (ObjectResult orLink : or.getLink()) {
                arLink.addObjectResult(orLink);
            }
            actionResults.add(arLink);
            or.getActionResults().addAll(actionResults);
        }
    }

    public  List<TripleObject> getTripleObjects() {
        return tripleObjects;
    }

    public  Map<String, Map<String, Map<String, Map<String, TripleObject>>>> getTriplesMap() {
        return triplesMap;
    }

    public  List<Node> getNodes() {
        return nodes;
    }

    public JobRegistry getJobRegistry() {
        return jobRegistry;
    }

}
