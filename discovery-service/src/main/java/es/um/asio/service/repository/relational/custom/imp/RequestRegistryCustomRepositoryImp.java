package es.um.asio.service.repository.relational.custom.imp;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.repository.relational.custom.RequestRegistryCustomRepository;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
public class RequestRegistryCustomRepositoryImp implements RequestRegistryCustomRepository {

    @PersistenceUnit()
    private EntityManagerFactory entityManagerFactory;

    @Override
    public Long persist(RequestRegistry o) {

        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        RequestRegistry aux;
        if (o.getId() == 0) {
            aux = em.merge(o);
            o.setId(aux.getId());
        } else {
            aux = em.find(RequestRegistry.class, o.getId());
            aux.copy(o);
            em.merge(aux);
        }
        em.flush();
        em.detach(aux);
        em.getTransaction().commit();
        em.clear();
        em.close();
        return o.getId();
    }

    @Override
    public List<Tuple> getRequestRegistriesByUserId(String userId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Query q = em.createNativeQuery("SELECT a.request_type,b.class_name,a.request_code,a.request_date,a.user_id \n" +
                "FROM discovery.request_registry a \n" +
                "left join discovery.job_registry b on b.id = a.jobRegistry_id\n" +
                "WHERE a.user_id = :userId\n" +
                "order by a.request_date DESC;", Tuple.class);
        q.setParameter("userId",userId);
        List<Tuple> results = q.getResultList();
        em.clear();
        em.close();
        return results;
    }
}
