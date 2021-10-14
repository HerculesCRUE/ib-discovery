package es.um.asio.service.repository.relational.custom.imp;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;
import es.um.asio.service.repository.relational.custom.JobRegistryCustomRepository;
import es.um.asio.service.repository.relational.custom.RequestRegistryCustomRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

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
}
