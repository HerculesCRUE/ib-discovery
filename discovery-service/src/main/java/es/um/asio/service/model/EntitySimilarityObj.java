package es.um.asio.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class EntitySimilarityObj {

    public float similarity;
    public TripleObject tripleObject;
    public Map<String,SimilarityValue> similarities;

    public EntitySimilarityObj(TripleObject to) {
        this.tripleObject = to;
        similarities = new HashMap<>();
    }

}
