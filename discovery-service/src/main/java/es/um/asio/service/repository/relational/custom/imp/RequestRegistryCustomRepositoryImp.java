package es.um.asio.service.repository.relational.custom.imp;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.repository.relational.custom.RequestRegistryCustomRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

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
}
