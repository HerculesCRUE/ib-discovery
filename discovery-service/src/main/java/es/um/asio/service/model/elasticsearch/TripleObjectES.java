package es.um.asio.service.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.springframework.data.elasticsearch.annotations.Score;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Document(indexName = "triple-object", type = "classes", shards = 2)
@Data
@AllArgsConstructor
public class TripleObjectES implements Comparable<TripleObjectES>{

    @Id
    private String id;
    private String className;
    @Field(type = FieldType.Date)
    private Date lastModification;
    @Field(type = FieldType.Object)
    private TripleStore tripleStore;
    @Field(type = FieldType.Object)
    private LinkedTreeMap<String,Object> attributes;
    @Score
    @JsonIgnore
    private float score;

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

    @Override
    public int compareTo(TripleObjectES o) {
        return ((Float) o.getScore()).compareTo((Float) this.getScore());
    }

    public static List<TripleObject> getTripleObjects(List<TripleObjectES> tosES) {
        List<TripleObject> tos = new ArrayList<>();
        if (tosES!=null) {
            for (TripleObjectES toES :tosES) {
                tos.add(new TripleObject(toES));
            }
        }
        return tos;
    }
}
