package es.um.asio.service.repository.relational.custom.imp;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.model.relational.RequestType;
import es.um.asio.service.repository.relational.custom.JobRegistryCustomRepository;
import es.um.asio.service.repository.relational.custom.RequestRegistryCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JobRegistryCustomRepositoryImp implements JobRegistryCustomRepository {

    @PersistenceUnit()
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    RequestRegistryCustomRepository requestRegistryCustomRepository;

    @Override
    public String persist(JobRegistry jr, boolean cascade) {

        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        JobRegistry jrAux;
        if (jr.getId() == null) { // Si es una actualizacion
            jrAux = em.merge(jr);
            jr.setId(jrAux.getId());
            jr.copy(jrAux);
        } else {
            jrAux = em.find(JobRegistry.class, jr.getId());
            jrAux.copy(jr);
            em.merge(jrAux);
        }
        em.flush();
        em.detach(jrAux);
        em.getTransaction().commit();
        em.clear();
        em.close();
        return jr.getId();
    }

    @Override
    public List<Tuple>  getResultsByUserIdAndRequestCodeAndRequestTypeNoNested(String userId, String requestCode, RequestType requestType) {
        JobRegistry jobRegistry = null;

        EntityManager em = entityManagerFactory.createEntityManager();
        Query q = em.createNativeQuery("SELECT a.id as jr_id,a.body_request as jr_body_request, a.class_name as jr_class_name,a.completion_date as jr_completion_date,a.data_source as jr_data_source,a.do_synchronous as jr_do_synchronous, a.is_completed as jr_is_completed, a.is_started as jr_is_started, a.node as jr_node, a.search_from_delta as jr_search_from_delta, a.search_links as jr_search_links, a.started_date as jr_started_date, a.status_result as jr_status_result, a.triple_store as jr_triple_store, a.version as jr_version, a.discoveryApplication_id as jr_discoveryApplication_id,\n" +
                "b.id as rr_id, b.email as rr_email, b.propague_in_kafka as rr_propague_in_kafka, b.request_code as rr_request_code, b.request_date as rr_request_date, b.request_type as rr_request_type, b.user_id as rr_user_id , b.version as rr_version, b.web_hook as rr_web_hook, b.jobRegistry_id as rr_jobRegistry_id,\n" +
                "c.id as or_id, c.canonical_uri as or_canonical_uri, c.class_name as or_class_name, c.entity_id as or_entity_id, c.is_automatic as or_is_automatic, c.is_link as or_is_link, c.is_main as or_is_main, c.is_manual as or_is_manual,  c.is_merge as or_is_merge,  c.last_modification as or_last_modification, c.local_uri as or_local_uri, c.merge_action as or_merge_action, c.node as or_node, c.origin as or_origin, c.similarity as or_similarity, c.similarity_no_id as or_similarity_no_id, c.state as or_state, c.triple_store as or_triple_store, c.version as or_version, c.actionResultParent_id as or_actionResultParent_id, c.jobRegistry_id as or_jobRegistry_id, c.parentAutomatic_id as or_parentAutomatic_id, c.parentLink_id as or_parentLink_id,  c.parentManual_id as or_parentManual_id,\n" +
                "d.id  as at_id, d.`key` as at_key, d.version as at_version, d.objectResult_id as at_objectResult_id, d.parentValue_id as at_parentValue_id,\n" +
                "e.id as va_id, e.`type` as va_type, e.val as va_va, e.version as va_version, e.attribute_id as va_attribute_id\n" +
                "FROM discovery.job_registry a\n" +
                "right join discovery.request_registry b on a.id=b.jobRegistry_id\n" +
                "left join discovery.object_result c on  a.class_name=c.class_name and c.is_main = 1 and a.id = c.jobRegistry_id\n" +
                "left join discovery.attribute d on c.id = d.objectResult_id\n" +
                "left join discovery.val e on d.id = e.attribute_id\n" +
                "where b.request_code = :requestCode and b.user_id = :userId and b.request_type = :requestType\n" +
                // "where b.request_code = :requestCode and b.user_id = :userId and b.request_type = :requestType\n" +
                "ORDER BY c.id,d.id,e.id;", Tuple.class);

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


    public class JobRequestRowMapper implements RowMapper {

        private Map<String,JobRegistry> jobRegistryMap;

        public JobRequestRowMapper() {
            this.jobRegistryMap = new HashMap<>();
        }

        @Override
        public Object mapRow(ResultSet rs, int i) throws SQLException {
            String id = rs.getString("jr_id");
            return null;
        }
    }
}
