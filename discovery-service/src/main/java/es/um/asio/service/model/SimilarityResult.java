package es.um.asio.service.model;

import es.um.asio.service.comparators.entities.EntitySimilarity;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class SimilarityResult {

    private TripleObject tripleObject;
    private Set<EntitySimilarityObj> automatic;
    private Set<EntitySimilarityObj> manual;

    public SimilarityResult(TripleObject tripleObject) {
        this.tripleObject = tripleObject;
        this.automatic = new HashSet<>();
        this.manual = new HashSet<>();
    }

    public void addAutomatics(List<EntitySimilarityObj> a) {
        if (a!=null)
            this.automatic.addAll(a);
    }

    public void addManuals(List<EntitySimilarityObj> m) {
        if (m!=null)
            this.manual.addAll(m);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimilarityResult that = (SimilarityResult) o;
        return Objects.equals(tripleObject, that.tripleObject) &&
                Objects.equals(automatic, that.automatic) &&
                Objects.equals(manual, that.manual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripleObject.getId());
    }
}
