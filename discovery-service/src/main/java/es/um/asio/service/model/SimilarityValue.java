package es.um.asio.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class SimilarityValue {
    public float similarity;
    public float weight;
}
