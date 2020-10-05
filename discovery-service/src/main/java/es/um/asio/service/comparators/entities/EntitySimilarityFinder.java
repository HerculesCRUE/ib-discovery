package es.um.asio.service.comparators.entities;


import es.um.asio.service.model.EntitySimilarityObj;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitySimilarityFinder {

    private final static Logger logger = LoggerFactory.getLogger(EntitySimilarityFinder.class);
    public static Map<String, List<EntitySimilarityObj>> findSimilarity(
            CacheServiceImp cache,
            TripleObject tripleObject,
            List<TripleObject> tripleObjects,
            double manualThreshold,
            double automaticThreshold
            ) {
        Map<String, List<EntitySimilarityObj>> similarities = new HashMap<>();
        similarities.put("automatic",new ArrayList<>());
        similarities.put("manual",new ArrayList<>());
        int counter = 0;
        float maxSimilarity = Float.MIN_VALUE;
        EntitySimilarityObj maxSimilarityObj;
        for (TripleObject other : tripleObjects) {
            counter++;
            EntitySimilarityObj entitySimilarityObj = tripleObject.compare(cache,other);
            if (entitySimilarityObj.getSimilarity() >= automaticThreshold)
                similarities.get("automatic").add(entitySimilarityObj);
            else if (entitySimilarityObj.getSimilarity() >= manualThreshold)
                similarities.get("manual").add(entitySimilarityObj);
            if (entitySimilarityObj.getSimilarity() > maxSimilarity) {
                maxSimilarity = entitySimilarityObj.getSimilarity();
                maxSimilarityObj = entitySimilarityObj;
            }
            if ((counter%100) == 0) {
                logger.info("Processed {} of {}, found {} automatics, {} manual similarities, MaxSimilarity: {}", counter, tripleObjects.size(), similarities.get("automatic").size(), similarities.get("manual").size(), maxSimilarity);
            }
        }
        return similarities;
    }

}
