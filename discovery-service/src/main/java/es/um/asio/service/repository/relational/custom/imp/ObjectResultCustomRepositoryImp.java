package es.um.asio.service.repository.relational.custom.imp;

import es.um.asio.service.model.relational.RequestType;

import es.um.asio.service.repository.relational.custom.ObjectResultCustomRepository;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
public class ObjectResultCustomRepositoryImp implements ObjectResultCustomRepository {

    @PersistenceUnit()
    private EntityManagerFactory entityManagerFactory;


    @Override
    public List<Tuple>  getDependentObjectResultByRequestRegistry(String userId, String requestCode, RequestType requestType) {

        EntityManager em = entityManagerFactory.createEntityManager();
        Query q1 = em.createNativeQuery("SELECT \n" +
                "b.id as orm_id, \n" +
                "c.id as or_id, c.canonical_uri as or_canonical_uri, c.class_name as or_class_name, c.entity_id as or_entity_id, c.is_automatic as or_is_automatic, c.is_link as or_is_link, c.is_main as or_is_main, c.is_manual as or_is_manual,  c.is_merge as or_is_merge,  c.last_modification as or_last_modification, c.local_uri as or_local_uri, c.merge_action as or_merge_action, c.node as or_node, c.origin as or_origin, c.similarity as or_similarity, c.similarity_no_id as or_similarity_no_id, c.state as or_state, c.triple_store as or_triple_store, c.version as or_version, c.actionResultParent_id as or_actionResultParent_id, c.jobRegistry_id as or_jobRegistry_id, c.parentAutomatic_id as or_parentAutomatic_id, c.parentLink_id as or_parentLink_id,  c.parentManual_id as or_parentManual_id,\n" +
                "d.id  as at_id, d.`key` as at_key, d.version as at_version, d.objectResult_id as at_objectResult_id, d.parentValue_id as at_parentValue_id,\n" +
                "e.id as va_id, e.val as va_va, e.type as va_type, e.version as va_version, e.attribute_id as va_attribute_id\n" +
                "FROM discovery.job_registry a\n" +
                "right join discovery.request_registry rr on a.id=rr.jobRegistry_id\n" +
                "left join discovery.object_result b on a.id = b.jobRegistry_id\n" +
                "right join discovery.object_result c on (c.is_manual=1 and b.id = c.parentManual_id)\n" +
                "left join discovery.attribute d on c.id = d.objectResult_id\n" +
                "left join discovery.val e on d.id = e.attribute_id\n" +
                "where rr.request_code = :requestCode and rr.user_id = :userId and rr.request_type = :requestType\n", Tuple.class );
        // Query q = em.createNativeQuery("SELECT a.id as id,a.version as version from discovery.job_registry as a");
        q1.setParameter("userId",userId);
        q1.setParameter("requestCode",requestCode);
        q1.setParameter("requestType",requestType.toString());
        List<Tuple> results = q1.getResultList();

        Query q2 = em.createNativeQuery("SELECT \n" +
                "b.id as orm_id, \n" +
                "c.id as or_id, c.canonical_uri as or_canonical_uri, c.class_name as or_class_name, c.entity_id as or_entity_id, c.is_automatic as or_is_automatic, c.is_link as or_is_link, c.is_main as or_is_main, c.is_manual as or_is_manual,  c.is_merge as or_is_merge,  c.last_modification as or_last_modification, c.local_uri as or_local_uri, c.merge_action as or_merge_action, c.node as or_node, c.origin as or_origin, c.similarity as or_similarity, c.similarity_no_id as or_similarity_no_id, c.state as or_state, c.triple_store as or_triple_store, c.version as or_version, c.actionResultParent_id as or_actionResultParent_id, c.jobRegistry_id as or_jobRegistry_id, c.parentAutomatic_id as or_parentAutomatic_id, c.parentLink_id as or_parentLink_id,  c.parentManual_id as or_parentManual_id,\n" +
                "d.id  as at_id, d.`key` as at_key, d.version as at_version, d.objectResult_id as at_objectResult_id, d.parentValue_id as at_parentValue_id,\n" +
                "e.id as va_id, e.type as va_type, e.val as va_va, e.version as va_version, e.attribute_id as va_attribute_id\n" +
                "FROM discovery.job_registry a\n" +
                "right join discovery.request_registry rr on a.id=rr.jobRegistry_id\n" +
                "left join discovery.object_result b on a.id = b.jobRegistry_id\n" +
                "right join discovery.object_result c on (c.is_manual=1 and b.id = c.parentAutomatic_id)\n" +
                "left join discovery.attribute d on c.id = d.objectResult_id\n" +
                "left join discovery.val e on d.id = e.attribute_id\n" +
                "where rr.request_code = :requestCode and rr.user_id = :userId and rr.request_type = :requestType\n", Tuple.class);
        // Query q = em.createNativeQuery("SELECT a.id as id,a.version as version from discovery.job_registry as a");
        q2.setParameter("userId",userId);
        q2.setParameter("requestCode",requestCode);
        q2.setParameter("requestType",requestType.toString());
        List<Tuple> resultsQ2 = q2.getResultList();

        Query q3 = em.createNativeQuery("SELECT \n" +
                "b.id as orm_id, \n" +
                "c.id as or_id, c.canonical_uri as or_canonical_uri, c.class_name as or_class_name, c.entity_id as or_entity_id, c.is_automatic as or_is_automatic, c.is_link as or_is_link, c.is_main as or_is_main, c.is_manual as or_is_manual,  c.is_merge as or_is_merge,  c.last_modification as or_last_modification, c.local_uri as or_local_uri, c.merge_action as or_merge_action, c.node as or_node, c.origin as or_origin, c.similarity as or_similarity, c.similarity_no_id as or_similarity_no_id, c.state as or_state, c.triple_store as or_triple_store, c.version as or_version, c.actionResultParent_id as or_actionResultParent_id, c.jobRegistry_id as or_jobRegistry_id, c.parentAutomatic_id as or_parentAutomatic_id, c.parentLink_id as or_parentLink_id,  c.parentManual_id as or_parentManual_id,\n" +
                "d.id  as at_id, d.`key` as at_key, d.version as at_version, d.objectResult_id as at_objectResult_id, d.parentValue_id as at_parentValue_id,\n" +
                "e.id as va_id, e.type as va_type, e.val as va_va, e.version as va_version, e.attribute_id as va_attribute_id\n" +
                "FROM discovery.job_registry a\n" +
                "right join discovery.request_registry rr on a.id=rr.jobRegistry_id\n" +
                "left join discovery.object_result b on a.id = b.jobRegistry_id\n" +
                "right join discovery.object_result c on (c.is_link=1 and b.id = c.parentLink_id)\n" +
                "left join discovery.attribute d on c.id = d.objectResult_id\n" +
                "left join discovery.val e on d.id = e.attribute_id\n" +
                "where rr.request_code = :requestCode and rr.user_id = :userId and rr.request_type = :requestType\n", Tuple.class );
        // Query q = em.createNativeQuery("SELECT a.id as id,a.version as version from discovery.job_registry as a");
        q3.setParameter("userId",userId);
        q3.setParameter("requestCode",requestCode);
        q3.setParameter("requestType",requestType.toString());
        List<Tuple> resultsQ3 = q3.getResultList();
        em.flush();
        em.clear();
        em.close();
        results.addAll(resultsQ2);
        results.addAll(resultsQ3);
        return results;
    }

}
