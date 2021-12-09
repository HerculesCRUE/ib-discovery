package data;

import es.um.asio.service.model.Node;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.relational.*;
import es.um.asio.service.service.impl.CacheServiceImp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.*;

@Getter
@Setter
public final class DataGenerator {

    private CacheServiceImp cacheServiceImp;
    private static List<TripleObject> tripleObjects;
    private static Map<String, Map<String, Map<String, Map<String, TripleObject>>>> triplesMap;
    private static List<Node> nodes;
    private static JobRegistry jobRegistry;

    public DataGenerator() throws Exception {
        tripleObjects = new ArrayList<>();
        triplesMap = new HashMap<>();
        nodes = new ArrayList<>();
        JSONObject jData = new JSONObject();
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                jData.put(String.format("att-%s", j), String.format("val-%s", j));
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
            if (!triplesMap.containsKey(to.getTripleStore().getNode().getNodeName()))
                triplesMap.put(to.getTripleStore().getNode().getNodeName(), new HashMap<>());
            if (!triplesMap.get(to.getTripleStore().getNode().getNodeName()).containsKey(to.getTripleStore().getName()))
                triplesMap.get(to.getTripleStore().getNode().getNodeName()).put(to.getTripleStore().getName(), new HashMap<>());
            if (!triplesMap.get(to.getTripleStore().getNode().getNodeName()).get(to.getTripleStore().getName()).containsKey(to.getClassName()))
                triplesMap.get(to.getTripleStore().getNode().getNodeName()).get(to.getTripleStore().getName()).put(to.getClassName(), new HashMap<>());
            triplesMap.get(to.getTripleStore().getNode().getNodeName()).get(to.getTripleStore().getName()).get(to.getClassName()).put(to.getId(),to);
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
                    new Date(),
                    null
            );
            jobRegistry.addRequestRegistry(rr);
        }
        // Añado todos los Triple Objects como Object Results
        int actionCounter = 0;
        for (TripleObject to :tripleObjects) {
            ObjectResult or = new ObjectResult(Origin.ASIO,State.CLOSED,jobRegistry,to,0f,0f);
            or.setId(Long.valueOf(to.getId()));
            jobRegistry.getObjectResults().add(or);
            float i = 0f;
            for (TripleObject toInner :tripleObjects) {
                i++;
                if (Math.abs(Integer.valueOf(to.getId()) - Integer.valueOf(toInner.getId())) <= 1) {
                    ObjectResult orInner = new ObjectResult(Origin.ASIO,State.CLOSED,null,toInner,1f,1f);
                    orInner.setId(Long.valueOf(toInner.getId()));
                    orInner.setParentAutomatic(or);
                    or.addAutomatic(orInner);
                } else if (Math.abs(Integer.valueOf(to.getId()) - Integer.valueOf(toInner.getId())) <= 2) {
                    ObjectResult orInner = new ObjectResult(Origin.ASIO,State.OPEN,null,toInner,0.9f -(i/10f),0.9f -(i/10f));
                    orInner.setId(Long.valueOf(toInner.getId()));
                    orInner.setParentManual(or);
                    or.addManual(orInner);
                } else {
                    ObjectResult orInner = new ObjectResult(Origin.ASIO,State.CLOSED,null,toInner,0.9f -(i/10f),0.9f -(i/10f));
                    orInner.setId(Long.valueOf(toInner.getId()));
                    orInner.setParentLink(or);
                    or.getLink().add(orInner);
                }
            }
            or.setStateFromChild();
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
        cacheServiceImp = new CacheServiceImp();
        cacheServiceImp.initialize();
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
