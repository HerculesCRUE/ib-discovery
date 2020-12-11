package es.um.asio.service.comparators.entities;

import es.um.asio.service.model.TripleObject;
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
