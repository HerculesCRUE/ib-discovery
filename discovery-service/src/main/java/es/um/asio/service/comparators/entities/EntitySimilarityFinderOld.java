package es.um.asio.service.comparators.entities;


import es.um.asio.service.model.EntitySimilarityObjOld;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitySimilarityFinderOld {

    private final static Logger logger = LoggerFactory.getLogger(EntitySimilarityFinderOld.class);
    public static Map<String, List<EntitySimilarityObjOld>> findSimilarity(
            CacheServiceImp cache,
            TripleObject tripleObject,
            List<TripleObject> tripleObjects,
            double manualThreshold,
            double automaticThreshold
            ) {
        Map<String, List<EntitySimilarityObjOld>> similarities = new HashMap<>();
        similarities.put("automatic",new ArrayList<>());
        similarities.put("manual",new ArrayList<>());
        int counter = 0;
        float maxSimilarity = Float.MIN_VALUE;
        EntitySimilarityObjOld maxSimilarityObj;
        for (TripleObject other : tripleObjects) {
            counter++;
            EntitySimilarityObjOld entitySimilarityObjOld = tripleObject.compare(cache,other);
            if (entitySimilarityObjOld.getSimilarity() >= automaticThreshold)
                similarities.get("automatic").add(entitySimilarityObjOld);
            else if (entitySimilarityObjOld.getSimilarity() >= manualThreshold)
                similarities.get("manual").add(entitySimilarityObjOld);
            if (entitySimilarityObjOld.getSimilarity() > maxSimilarity) {
                maxSimilarity = entitySimilarityObjOld.getSimilarity();
                maxSimilarityObj = entitySimilarityObjOld;
            }
            if ((counter%100) == 0) {
                logger.info("Processed {} of {}, found {} automatics, {} manual similarities, MaxSimilarity: {}", counter, tripleObjects.size(), similarities.get("automatic").size(), similarities.get("manual").size(), maxSimilarity);
            }
        }
        return similarities;
    }

}
