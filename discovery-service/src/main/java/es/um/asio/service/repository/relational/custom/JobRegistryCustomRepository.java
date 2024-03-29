package es.um.asio.service.repository.relational.custom;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestType;

import javax.persistence.Tuple;
import java.util.List;

public interface JobRegistryCustomRepository {

    public String persist(JobRegistry jr, boolean cascade);

    public List<Tuple> getResultsByUserIdAndRequestCodeAndRequestTypeNoNested(String userId, String requestCode, RequestType requestType);

}
