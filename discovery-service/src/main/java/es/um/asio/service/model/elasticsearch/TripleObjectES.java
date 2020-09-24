package es.um.asio.service.model.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "triple")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripleObjectES {

    @Id
    private String id;

    private String tittle;
}
