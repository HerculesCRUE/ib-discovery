package es.um.asio.service.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.TripleStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Score;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Document(indexName = "triple-object", type = "classes", shards = 10)
@Data
@AllArgsConstructor
public class TripleObjectES implements Comparable<TripleObjectES>{

    @Id
    private int id;
    private String entityId;
    private String localURI;
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

    public TripleObjectES(String id, String node, String tripleStore, String className, Date lastName) {
        attributes = new LinkedTreeMap();
        this.entityId = id;
        this.tripleStore = new TripleStore(tripleStore,node);
        this.id = generateComposedId();
        this.className = className;
        this.lastModification = lastName;
    }

    public TripleObjectES(TripleObject to) {
        this.entityId = to.getId();
        this.localURI = to.getLocalURI();
        this.className = to.getClassName();
        this.lastModification = new Date(to.getLastModification());
        this.attributes = to.getAttributes();
        this.tripleStore = to.getTripleStore();
        this.id = generateComposedId();
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

    private int generateComposedId() {
        return Objects.hash(entityId,className,tripleStore.getNode().getNode(),tripleStore.getTripleStore());
    }




}
