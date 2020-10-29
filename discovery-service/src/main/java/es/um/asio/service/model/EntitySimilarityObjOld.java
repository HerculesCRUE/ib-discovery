package es.um.asio.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class EntitySimilarityObjOld {

    public float similarity;
    public TripleObject tripleObject;
    public Map<String, SimilarityValueOld> similarities;

    public EntitySimilarityObjOld(TripleObject to) {
        this.tripleObject = to;
        similarities = new HashMap<>();
    }

}
