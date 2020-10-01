package es.um.asio.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "classes", type = "TripleObject")
public class TripleObject {

    @Expose(serialize = false, deserialize = false)
    @Transient
    private final Logger logger = LoggerFactory.getLogger(TripleObject.class);


    @Expose(serialize = true, deserialize = true)
    @Id
    private String id;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Text)
    private String className;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Date)
    private Date lastModification;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Object)
    private TripleStore tripleStore;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Object)
    private LinkedTreeMap<String,Object> attributes;

    public TripleObject(TripleObjectES toES) {
        this.id = toES.getId();
        this.className = toES.getClassName();
        this.lastModification = toES.getLastModification();
        this.tripleStore = toES.getTripleStore();
        this.attributes = toES.getAttributes();
    }
}
