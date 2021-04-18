package es.um.asio.service.service;

import es.um.asio.service.model.Decision;
import es.um.asio.service.model.relational.ActionResult;
import es.um.asio.service.model.relational.ObjectResult;

import java.util.List;
import java.util.Map;

public interface OpenSimilaritiesHandler {

    public List<ObjectResult> getOpenObjectResults(String node, String tripleStore);

    public Map<ObjectResult,List<ActionResult>> decisionOverObjectResult(String className, String entityIdMainObject, String entityIdRelatedObject, Decision decision);
}
