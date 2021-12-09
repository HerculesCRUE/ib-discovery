package es.um.asio.service.repository.relational.custom;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;

import javax.persistence.Tuple;
import java.util.List;

public interface RequestRegistryCustomRepository {

    public Long persist(RequestRegistry rr);

    public List<Tuple> getRequestRegistriesByUserId(String userId);

}
