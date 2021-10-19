package es.um.asio.service.repository.relational.custom;

import es.um.asio.service.model.relational.RequestType;

import javax.persistence.Tuple;
import java.util.List;

public interface ActionResultCustomRepository {

    public List<Tuple> getActionResultsByRequestRegistry(String userId, String requestCode, RequestType requestType);

}
