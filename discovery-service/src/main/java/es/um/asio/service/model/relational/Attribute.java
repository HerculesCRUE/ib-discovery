package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonElement;
import es.um.asio.service.util.Utils;
import lombok.*;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.w3c.dom.Attr;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = Attribute.TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {

    public static final String TABLE = "attribute";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Attribute.Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Column(name = Columns.KEY, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    private String key;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private ObjectResult objectResult;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attribute", cascade = CascadeType.ALL)
    private Set<Value> values;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Value parentValue;

    public Attribute(String key, Object value, ObjectResult objectResult) {
        this.key = key;
        this.objectResult = objectResult;
        this.values = new HashSet<>();
        if (value instanceof List) {
            for (Object v : (List) value){
                this.values.add(new Value(this,v));
            }
        } else { // Es una lista
            this.values.add(new Value(this,value));
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return Objects.equals(key, attribute.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    /**
     * Column name constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Columns {
        /**
         * ID column.
         */
        protected static final String ID = "id";
        /**
         * CLASS_NAME column.
         */
        protected static final String KEY = "`key`";
    }
}
