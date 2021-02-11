package es.um.asio.service.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TRIPLE_STORE")
@Getter
@ToString(includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TripleStore {

    /**
     * Version ID.
     */
    private static final long serialVersionUID = -8605786237765754617L;

    /**
     * The id.
     */
    @Id
/*    @Column(name = "id", nullable = false,columnDefinition = "VARCHAR(100)",length = 100)*/
    @EqualsAndHashCode.Include
    @Expose(serialize = true, deserialize = true)
    private String id;

    /**
     * The tripleStore.
     */
/*    @Column(name = "triple_store", nullable = false,columnDefinition = "VARCHAR(100)",length = 100)*/
    @EqualsAndHashCode.Include
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Keyword)
    private String name;

    /**
     * Node.
     * Relation Bidirectional ManyToOne
     */
/*    @ManyToOne(fetch = FetchType.LAZY)*/
    @Expose(serialize = true, deserialize = true)
    private Node node;

    /**
     * The baseURL.
     */
/*    @Column(name = "base_url", nullable = false,columnDefinition = "VARCHAR(400)",length = 400)*/
    @EqualsAndHashCode.Include
    @Expose(serialize = true, deserialize = true)
    private String baseURL;

    /**
     * The user.
     */
/*    @Column(name = "user", nullable = false,columnDefinition = "VARCHAR(100)",length = 100)*/
    @EqualsAndHashCode.Include
    @Expose(serialize = true, deserialize = true)
    private String user;

    /**
     * The password.
     */
/*    @Column(name = "password", nullable = false,columnDefinition = "VARCHAR(100)",length = 100)*/
    @ColumnTransformer(read = "pgp_sym_decrypt(password, 'asio-secret-key')", write = "pgp_sym_encrypt(?, 'asio-secret-key')")
    @EqualsAndHashCode.Include
    @Expose(serialize = true, deserialize = true)
    private String password;

    public TripleStore(String tripleStore, String node, String baseURL, String user, String password) {
        this.name = tripleStore;
        this.node = new Node(node);
        this.baseURL = baseURL;
        this.user = user;
        this.password = password;
    }

    public TripleStore(String tripleStore, String node) {
        this.name = tripleStore;
        this.node = new Node(node);
    }

    public TripleStore() {
    }

    public JsonObject toJson() {
        JsonObject jTs = new JsonObject();
        jTs.addProperty("id",getId());
        jTs.addProperty("name",getName());
        jTs.addProperty("baseURL",getBaseURL());
        jTs.addProperty("user",getUser());
        jTs.addProperty("password",getPassword());
        jTs.add("tripleStore",getNode().toJson());
        return jTs;
    }
}
