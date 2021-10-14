package es.um.asio.service.repository.relational.custom;

import es.um.asio.service.model.relational.JobRegistry;

public interface JobRegistryCustomRepository {

    public String persist(JobRegistry jr, boolean cascade);

}
