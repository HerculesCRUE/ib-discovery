package es.um.asio.service.comparators.entities;

import com.google.gson.Gson;
import es.um.asio.service.comparators.aggregators.AccordSimilarity;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class EntitySimilarityObj {

    public TripleObject tripleObject;
    public float similarity;
    public Map<String, SimilarityValue> similarities; // Att -> Value

    public EntitySimilarityObj(TripleObject to) {
        this.tripleObject = to;
        similarities = new HashMap<>();
    }

    public float getSimilarity() {
        if (similarity==0) {
            similarity = new ArrayList<SimilarityValue>(similarities.values()).stream().map(att -> att.weightedSimilarity).reduce(0f,Float::sum);
        }
        return similarity;
    }

    public void addSimilarity(String name, SimilarityValue similarityValue) {
        if (!similarities.containsKey(name)) {
            similarity += similarityValue.getWeightedSimilarity();
        } else {
            similarity -= similarities.get(name).getWeightedSimilarity();
            similarity += similarityValue.getWeightedSimilarity();
        }
        similarities.put(name,similarityValue);
    }

    public Map<String, SimilarityValue> getSimilarities() {
        return similarities;
    }
}
