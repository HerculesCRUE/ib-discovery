package es.um.asio.service.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import es.um.asio.service.model.relational.Value;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * NODE Class.
 * @see Value
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
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

    /**
     * Constructor
     * @param node String. Name of node
     */
    public Node(String node) {
        this.nodeName = node;
    }

    /**
     * Constructor
     */
    public Node() {
    }

    /**
     * Cast to JSON
     * @return JsonObject
     */
    public JsonObject toJson() {
        JsonObject jNode = new JsonObject();
        jNode.addProperty("nodeName", getNodeName());
        return jNode;
    }

}
