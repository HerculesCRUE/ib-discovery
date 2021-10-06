package es.um.asio.service.comparators.entities;

import es.um.asio.service.config.DataBehaviour;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class Model a EntitySimilarityObj with TripleObject target, Data source, similarity found and Map of similarities by attribute
 * @see TripleObject
 * @see SimilarityValue
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class EntitySimilarityObj {

    private TripleObject tripleObject;
    private String dataSource;
    private float similarity;
    private Map<String, SimilarityValue> similarities; // Att -> Value

    /**
     * Constructor
     * @see TripleObject
     * @param to TripleObject
     */
    public EntitySimilarityObj(TripleObject to) {
        this.tripleObject = to;
        similarities = new HashMap<>();
    }

    /**
     * Calculate the similarity from similarities structure
     * @return Float. the similarity
     */
    public float getSimilarity() {
        if (similarity==0) {
            similarity = new ArrayList<SimilarityValue>(similarities.values()).stream().map(SimilarityValue::getWeightedSimilarity).reduce(0f,Float::sum);
        }
        return similarity;
    }

    /**
     * Add a new similarity by name and SimilarityValue
     * @see SimilarityValue
     * @param name String. The name of the attribute.
     * @param similarityValue SimilarityValue. The value of the similarity.
     */
    public void addSimilarity(String name, SimilarityValue similarityValue) {
        if (!similarities.containsKey(name)) {
            similarity += similarityValue.getWeightedSimilarity();
        } else {
            similarity -= similarities.get(name).getWeightedSimilarity();
            similarity += similarityValue.getWeightedSimilarity();
        }
        similarities.put(name,similarityValue);
    }


    /**
     * Get the similarities
     * @see SimilarityValue
     * @return Map<String, SimilarityValue>. The key is the name of the attribute and the value is the SimilarityValue
     */
    public Map<String, SimilarityValue> getSimilarities() {
        return similarities;
    }

    /**
     * Get the weighted similarities without id attribute.
     * @return Float. The weighted similarities without id attribute.
     */
    public float getSimilarityWithoutId(DataBehaviour dataBehaviour,boolean excludeLinks) {
        float sumSimilarities = 0f;
        float sumWeight = 0f;
        for (Map.Entry<String, SimilarityValue> svEntry :similarities.entrySet()) {
            if (!Utils.isIdFormat(svEntry.getKey()) && (!excludeLinks || !dataBehaviour.ignoreAttribute(tripleObject.getClassName(),svEntry.getKey()) /*|| !svEntry.getValue().isLink()*/ || svEntry.getValue().getSimilarity() == 1) ) {
                sumSimilarities += svEntry.getValue().getWeightedSimilarity();
                sumWeight += svEntry.getValue().getWeight();
            }
        }
        return (sumWeight!=0)?sumSimilarities/sumWeight:similarity;
    }

    /**
     * Check if the Object pass is equal to TripleObject attribute.
     * @param o Object. The object to compare
     * @return Boolean. True if equal else False
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitySimilarityObj that = (EntitySimilarityObj) o;
        return Float.compare(that.similarity, similarity) == 0 &&
                Objects.equals(tripleObject, that.tripleObject) &&
                Objects.equals(similarities, that.similarities);
    }

    /**
     * @return int. The hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(tripleObject.getId());
    }
}
