package es.um.asio.service.repository.relational.custom;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestRegistry;

public interface RequestRegistryCustomRepository {

    public Long persist(RequestRegistry rr);

}
