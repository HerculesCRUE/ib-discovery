package es.um.asio.service.model;

import com.google.api.client.json.Json;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "NODE")
@Getter
@ToString(includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Node {

    /**
     * The Node.
     */
    @Id
    @EqualsAndHashCode.Include
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Keyword)
    private String nodeName;

    public Node(String node) {
        this.nodeName = node;
    }

    public Node() {
    }

    public JsonObject toJson() {
        JsonObject jNode = new JsonObject();
        jNode.addProperty("nodeName", getNodeName());
        return jNode;
    }

}
