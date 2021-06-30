package es.um.asio.service.service.impl;

import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.Decision;
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
        List<ObjectResult> omor = oMainOR.get();
        for (ObjectResult orm : oMainOR.get()) { // Por cada Main Object Result encontrado
            if (!toPropague.containsKey(orm)) {
                toPropague.put(orm,new ArrayList<>());
            }
            @Nullable
            ObjectResult or = null;
            Set<ObjectResult> manuales = orm.getManual();
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
                        ObjectResult toUpdateAux = new ObjectResult(Origin.ASIO,State.CLOSED,null, toUpdate.toTripleObject(orm.getJobRegistry()).merge(or.toTripleObject(orm.getJobRegistry())), or.getSimilarity(), or.getSimilarityWithOutId());
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

                    // Actions
                    orm.setStateFromChild();
                    if (toUpdate != null) {
                        ActionResult actionResult = new ActionResult(Action.UPDATE, orm);
                        actionResult.addObjectResult(toUpdate);
                        orm.getActionResults().add(actionResult);
                        toUpdate.setActionResultParent(actionResult);
                        toPropague.get(orm).add(actionResult);
                        actionResultRepository.save(actionResult);
                    }

                    if (toDelete != null && !toDelete.isEmpty()) {
                        ActionResult actionResult = new ActionResult(Action.DELETE, orm);
                        for (ObjectResult orDelete : toDelete) {
                            actionResult.addObjectResult(orDelete);
                            orDelete.setActionResultParent(actionResult);
                        }
                        if (!actionResult.getObjectResults().isEmpty()) {
                            orm.getActionResults().add(actionResult);
                        }
                        toPropague.get(orm).add(actionResult);
                        actionResultRepository.save(actionResult);
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
