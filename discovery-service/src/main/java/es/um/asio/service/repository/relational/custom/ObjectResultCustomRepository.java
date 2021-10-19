package es.um.asio.service.repository.relational.custom;

import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.RequestType;

import javax.persistence.Tuple;
import java.util.List;

public interface ObjectResultCustomRepository {

    public List<Tuple> getDependentObjectResultByRequestRegistry(String userId, String requestCode, RequestType requestType);

}
