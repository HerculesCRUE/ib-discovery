package es.um.asio.service.comparators.entities;

import es.um.asio.service.model.TripleObject;
import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class EntitySimilarityObj {

    private TripleObject tripleObject;
    private String dataSource;
    private float similarity;
    private Map<String, SimilarityValue> similarities; // Att -> Value

    public EntitySimilarityObj(TripleObject to) {
        this.tripleObject = to;
        similarities = new HashMap<>();
    }

    public float getSimilarity() {
        if (similarity==0) {
            similarity = new ArrayList<SimilarityValue>(similarities.values()).stream().map(SimilarityValue::getWeightedSimilarity).reduce(0f,Float::sum);
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

    public float getSimilarityWithoutId() {
        float sumSimilarities = 0f;
        float sumWeight = 0f;
        for (Map.Entry<String, SimilarityValue> svEntry :similarities.entrySet()) {
            if (!Utils.isIdFormat(svEntry.getKey()) ) {
                sumSimilarities += svEntry.getValue().getWeightedSimilarity();
                sumWeight += svEntry.getValue().getWeight();
            }
        }
        return (sumWeight!=0)?sumSimilarities/sumWeight:similarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitySimilarityObj that = (EntitySimilarityObj) o;
        return Float.compare(that.similarity, similarity) == 0 &&
                Objects.equals(tripleObject, that.tripleObject) &&
                Objects.equals(similarities, that.similarities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripleObject.getId());
    }
}
