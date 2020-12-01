package es.um.asio.service.model;

import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
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
    @Column(name = "node",columnDefinition = "VARCHAR(100)")
    @EqualsAndHashCode.Include
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Keyword)
    private String node;

    public Node(String node) {
        this.node = node;
    }

    public Node() {
    }
}
