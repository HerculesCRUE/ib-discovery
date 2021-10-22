package es.um.asio.service.repository.relational.custom.imp;

import es.um.asio.service.model.relational.RequestType;
import es.um.asio.service.repository.relational.custom.ActionResultCustomRepository;
import es.um.asio.service.repository.relational.custom.ObjectResultCustomRepository;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
public class ActionResultCustomRepositoryImp implements ActionResultCustomRepository {

    @PersistenceUnit()
    private EntityManagerFactory entityManagerFactory;


    @Override
    public List<Tuple>  getActionResultsByRequestRegistry(String userId, String requestCode, RequestType requestType) {

        EntityManager em = entityManagerFactory.createEntityManager();

        Query q = em.createNativeQuery("SELECT b.id as p_or_id,\n" +
                "c.id as ac_id, c.`action` as ac_action, c.version as ac_version, \n" +
                "d.id as or_id, d.canonical_uri as or_canonical_uri, d.class_name as or_class_name, d.entity_id as or_entity_id, d.is_automatic as or_is_automatic, d.is_link as or_is_link, d.is_main as or_is_main, d.is_manual as or_is_manual,  d.is_merge as or_is_merge,  d.last_modification as or_last_modification, d.local_uri as or_local_uri, d.merge_action as or_merge_action, d.node as or_node, d.origin as or_origin, d.similarity as or_similarity, d.similarity_no_id as or_similarity_no_id, d.state as or_state, d.triple_store as or_triple_store, d.version as or_version, d.actionResultParent_id as or_actionResultParent_id, d.jobRegistry_id as or_jobRegistry_id, d.parentAutomatic_id as or_parentAutomatic_id, d.parentLink_id as or_parentLink_id,  d.parentManual_id as or_parentManual_id,\n" +
                "e.id  as at_id, e.`key` as at_key, e.version as at_version, e.objectResult_id as at_objectResult_id, e.parentValue_id as at_parentValue_id,\n" +
                "f.id as va_id, f.type as va_type,f.val as va_va, f.version as va_version, f.attribute_id as va_attribute_id\n" +
                "FROM discovery.job_registry a\n" +
                "right join discovery.request_registry rr on a.id=rr.jobRegistry_id\n" +
                "left join discovery.object_result b on a.id = b.jobRegistry_id\n" +
                "right join discovery.action_result c on c.objectResultParent_id = b.id\n" +
                "left join discovery.object_result d on d.actionResultParent_id = c.id\n" +
                "left join discovery.attribute e on d.id = e.objectResult_id\n" +
                "left join discovery.val f on e.id = f.attribute_id\n" +
                "where rr.request_code = :requestCode and rr.user_id = :userId and rr.request_type = :requestType\n", Tuple.class);
        // Query q = em.createNativeQuery("SELECT a.id as id,a.version as version from discovery.job_registry as a");
        q.setParameter("userId",userId);
        q.setParameter("requestCode",requestCode);
        q.setParameter("requestType",requestType.toString());
        List<Tuple> results = q.getResultList();
        em.flush();
        em.clear();
        em.close();
        return results;
    }

}
