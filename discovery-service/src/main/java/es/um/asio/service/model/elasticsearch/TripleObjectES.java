package es.um.asio.service.model.elasticsearch;

import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.HashMap;

@Document(indexName = "triple-object", type = "classes", shards = 2)
@Data
@AllArgsConstructor
public class TripleObjectES {

    @Id
    private String id;
    private String className;
    @Field(type = FieldType.Date)
    private Date lastModification;
    @Field(type = FieldType.Object)
    private TripleStore tripleStore;
    @Field(type = FieldType.Object)
    private LinkedTreeMap<String,Object> attributes;

    public TripleObjectES() {
        attributes = new LinkedTreeMap();
    }

    public TripleObjectES(String id, String className, Date lastName) {
        attributes = new LinkedTreeMap();
        this.id = id;
        this.className = className;
        this.lastModification = lastName;
    }

    public TripleObjectES(TripleObject to) {
        this.id = to.getId();
        this.className = to.getClassName();
        this.lastModification = new Date(to.getLastModification());
        this.attributes = to.getAttributes();
        this.tripleStore = to.getTripleStore();
    }


}
