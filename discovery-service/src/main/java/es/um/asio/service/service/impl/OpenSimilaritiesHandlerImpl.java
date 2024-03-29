package es.um.asio.service.service.impl;

import es.um.asio.service.config.Hierarchies;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.Decision;
import es.um.asio.service.model.MergeType;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.relational.*;
import es.um.asio.service.repository.relational.ActionResultRepository;
import es.um.asio.service.repository.relational.ObjectResultRepository;
import es.um.asio.service.service.OpenSimilaritiesHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;

@Service
public class OpenSimilaritiesHandlerImpl implements OpenSimilaritiesHandler {

    @Autowired
    ObjectResultRepository repository;

    @Autowired
    ActionResultRepository actionResultRepository;


    @Autowired
    JobHandlerServiceImp jobHandlerServiceImp;

    @Autowired
    Hierarchies hierarchies;

    @Autowired
    CacheServiceImp cache;

    @Override
    public List<ObjectResult> getOpenObjectResults(String node, String tripleStore) {
        return repository.getOpenObjectResults(node,tripleStore);
    }

    @Override
    public Map<ObjectResult,List<ActionResult>> decisionOverObjectResult(String className,String entityIdMainObject,String entityIdRelatedObject,Decision decision) {
        Map<ObjectResult,List<ActionResult>> toPropague = new HashMap<>();
        Optional<List<ObjectResult>> oMainOR = repository.findByEntityIdAndClassNameAndIsMain(entityIdMainObject,className, true);
        if (oMainOR.isEmpty()) {
            throw new CustomDiscoveryException(String.format("Entity of class: %s with entityId: %s not found",className,entityIdMainObject));
        }
        for (ObjectResult orm : oMainOR.get()) { // Por cada Main Object Result encontrado
            if (!toPropague.containsKey(orm)) {
                toPropague.put(orm,new ArrayList<>());
            }
            @Nullable
            ObjectResult or = null;
            for (ObjectResult orr : orm.getManual()) { // Para todos los manuales
                if (orr.getEntityId().equals(entityIdRelatedObject)) {
                    or = orr;
                    break;
                }
            }
            if (null == or || or.getState() == State.CLOSED) {
                // throw new CustomDiscoveryException(String.format("Entity of class: %s with entityId: %s not found related entity with entityId: %s or State is closed: %s",className,entityIdMainObject,entityIdRelatedObject,or.getState().toString()));
            } else {
                if (decision == Decision.DISCARDED) {
                    or.setState(State.DISCARDED);
                    repository.save(or);
                } else { // Si la decisión es Aceptada

                    // Merges
                    ObjectResult toUpdate = orm; // La entidad que se actualiza es la main
                    Set<ObjectResult> toDelete = new HashSet<>(); // A eliminar
                    Set<ObjectResult> toLink = new HashSet<>(); // A linkar
                    or.setState(State.CLOSED);
                    repository.save(or);
                    if ( // Si es el mismo nodo y el mismo triple store
                            orm.getNode().equals(or.getNode()) &&
                            orm.getTripleStore().equals(or.getTripleStore())
                    ) { // Entonces añado a borrar
                        ObjectResult toUpdateAux = new ObjectResult(Origin.ASIO,State.CLOSED,null, toUpdate.toTripleObject(orm.getJobRegistry()).merge(or.toTripleObject(orm.getJobRegistry()),hierarchies,cache, (decision == Decision.ACCEPTED)? MergeType.MAIN: MergeType.OTHER ), or.getSimilarity(), or.getSimilarityWithOutId());
                        if (!toUpdateAux.getEntityId().equals(toUpdate.getEntityId())) {
                            toDelete.remove(toUpdateAux);
                            toDelete.add(toUpdate);
                            toUpdate = toUpdateAux;
                        } else {
                            toDelete.add(or);
                        }
                    } else {
                        toLink.add(or);
                    }
                    orm.setActionResults(new HashSet<>()); // Inicializo action results


                    // Actions
                    orm.setStateFromChild();
                    ActionResult actionResultUpdate = null;
                    orm.setActionResults(new HashSet<>());
                    if (toUpdate != null) {
                        actionResultUpdate = new ActionResult(Action.UPDATE, orm);
                        actionResultUpdate.addObjectResult(toUpdate);
                        orm.getActionResults().add(actionResultUpdate);
                        toUpdate.setActionResultParent(actionResultUpdate);
                        toPropague.get(orm).add(actionResultUpdate);
                        actionResultRepository.save(actionResultUpdate);
                    }

                    if (toDelete != null && !toDelete.isEmpty()) {

                        JobRegistry jobRegistry = or.getJobRegistry();
                        ActionResult actionResult = new ActionResult(Action.DELETE, orm);
                        for (ObjectResult orDelete : toDelete) {
                            actionResult.addObjectResult(orDelete);
                            orDelete.setActionResultParent(actionResult);
                            toPropague.get(orm).add(actionResult);
                            Map<String, org.javatuples.Pair<String, TripleObject>> dependencies = cache.getLinksToTripleObject(orDelete.toTripleObject(jobRegistry));
                            if (dependencies.size()>0) { // Para los borrados hay que mover los enlaces
                                for (Map.Entry<String, org.javatuples.Pair<String,TripleObject>> dep :dependencies.entrySet()) {
                                    TripleObject toUpdateLink = dep.getValue().getValue1();
                                    String key = dep.getValue().getValue0();
                                    String toUpdateCanonicalURI = toUpdate.getCanonicalURI();
                                    String toRemoveCanonicalURI = orDelete.getCanonicalURI();
                                    toUpdateLink.replaceAttValue(key,toRemoveCanonicalURI,toUpdateCanonicalURI); // Cambio los enlaces
                                    // Creo el objectResult para actualizaciones de dependencias
                                    ObjectResult orUpdateLink = new ObjectResult(Origin.ASIO,State.CLOSED,jobRegistry, toUpdateLink, null,null);
                                    actionResultUpdate.addObjectResult(orUpdateLink);
                                    orUpdateLink.setActionResultParent(actionResultUpdate);
                                }
                            }
                        }
                        if (!actionResult.getObjectResults().isEmpty())
                            orm.getActionResults().add(actionResult);

                    }

                    if (toLink != null && !toLink.isEmpty()) {
                        ActionResult actionResult = new ActionResult(Action.LINK, orm);
                        for (ObjectResult orLink : toLink) {
                            actionResult.addObjectResult(orLink);
                            orLink.setActionResultParent(actionResult);
                        }
                        orm.getActionResults().add(actionResult);
                        toPropague.get(orm).add(actionResult);
                        actionResultRepository.save(actionResult);
                    }
                }
            }
            orm.setStateFromChild();
            repository.save(orm);
        } // End first for
        jobHandlerServiceImp.propagueKafkaActions(toPropague);
        return toPropague;
    }
}
