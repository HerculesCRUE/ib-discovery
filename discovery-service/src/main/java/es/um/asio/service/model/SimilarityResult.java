package es.um.asio.service.model;

import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * SimilarityResult Class.
 * @see EntitySimilarityObj
 * @see TripleObject
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class SimilarityResult {

    private TripleObject tripleObject;
    private Set<EntitySimilarityObj> automatic;
    private Set<EntitySimilarityObj> manual;

    /**
     * Constructor
     * @param tripleObject TripleObject. Triple Object for SimilarityResult
     */
    public SimilarityResult(TripleObject tripleObject) {
        this.tripleObject = tripleObject;
        this.automatic = new HashSet<>();
        this.manual = new HashSet<>();
    }

    /**
     * Add automatic EntitySimilarityObj to SimilarityResult
     * @param a List<EntitySimilarityObj> List of Entity Similarity Objects to add to automatics
     */
    public void addAutomatics(List<EntitySimilarityObj> a) {
        if (a!=null)
            this.automatic.addAll(a);
    }

    /**
     * Add automatic EntitySimilarityObj to SimilarityResult
     * @param a List<EntitySimilarityObj> List of Entity Similarity Objects to add to manuals
     */
    public void addManuals(List<EntitySimilarityObj> m) {
        if (m!=null)
            this.manual.addAll(m);
    }

    /**
     * Equals
     * @param o Object (SimilarityResult)
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimilarityResult that = (SimilarityResult) o;
        return Objects.equals(tripleObject, that.tripleObject) &&
                Objects.equals(automatic, that.automatic) &&
                Objects.equals(manual, that.manual);
    }

    /**
     * hashCode
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(tripleObject.getId());
    }
}
