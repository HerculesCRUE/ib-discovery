package es.um.asio.service.comparators.entities;


import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.service.impl.CacheServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the Entity Similarity Handler
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Component
public class EntitySimilarityHandler {

    private static final Logger logger = LoggerFactory.getLogger(EntitySimilarityHandler.class);
    private static final String MANUAL_KEY="MANUAL";
    private static final String AUTOMATIC_KEY="AUTOMATIC";

    @Autowired
    CacheServiceImp cache;

    @Autowired
    DataSourcesConfiguration dataSourcesConfiguration;

    /**
     * This method calculate the similitude between the TripleObject pass in parameter with the other TripleObjects pass in parameter, and use Thresholds for manual and automatic cataloging of similarities
     * @see EntitySimilarityObj
     * @see CacheServiceImp
     * @see TripleObject
     * @param cache Cache The cache objects with the TripleObjects stored
     * @param tripleObject TripleObject. The TripleObject to evaluate the similitude with the rest
     * @param tripleObjects List<TripleObject>. The rest of TripleObjects to evaluate the similitude with TripleObject pass in parameter
     * @param manualThreshold Double. The  manual Threshold
     * @param automaticThreshold Double. The  automatic Threshold
     * @return Map<String, List<EntitySimilarityObj>> with the key as MANUAL or AUTOMATIC, and the value the EntitySimilarityObj found
     */
    public static Map<String, List<EntitySimilarityObj>> calculateSimilarityInEntities(
            CacheServiceImp cache,
            TripleObject tripleObject,
            List<TripleObject> tripleObjects,
            double manualThreshold,
            double automaticThreshold
            ) {
        Map<String, List<EntitySimilarityObj>> similarities = new HashMap<>();
        similarities.put(MANUAL_KEY,new ArrayList<>());
        similarities.put(AUTOMATIC_KEY,new ArrayList<>());
        int counter = 0;
        float maxSimilarity = Float.MIN_VALUE;
        for (TripleObject other : tripleObjects) {
            counter++;
            EntitySimilarityObj entitySimilarityObj = tripleObject.compare(cache,other);
            if (entitySimilarityObj.getSimilarity() >= automaticThreshold)
                similarities.get(AUTOMATIC_KEY).add(entitySimilarityObj);
            else if (entitySimilarityObj.getSimilarity() >= manualThreshold)
                similarities.get(MANUAL_KEY).add(entitySimilarityObj);
            if (entitySimilarityObj.getSimilarity() > maxSimilarity) {
                maxSimilarity = entitySimilarityObj.getSimilarity();
            }
            if ((counter%100) == 0) {
                logger.info("Processed {} of {}, found {} automatics, {} manual similarities, MaxSimilarity: {}", counter, tripleObjects.size(), similarities.get("automatic").size(), similarities.get("manual").size(), maxSimilarity);
            }
        }
        return similarities;
    }

}
