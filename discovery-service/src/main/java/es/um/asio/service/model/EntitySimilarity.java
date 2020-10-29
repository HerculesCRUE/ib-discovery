package es.um.asio.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class EntitySimilarity {

    public TripleObject tripleObject;
    public float similarity;
    public Map<String, SimilarityValue> similarities;
}
