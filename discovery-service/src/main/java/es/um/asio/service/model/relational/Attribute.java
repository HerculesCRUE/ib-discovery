package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Attribute Class. In relational model the Attribute entity.
 * @see Value
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Attribute.Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Version
    private Long version;

    @Column(name = Columns.KEY, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    private String key;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private ObjectResult objectResult;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "attribute", cascade = CascadeType.ALL)
    private Set<Value> values;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Value parentValue;

    /**
     * Constructor
     * @see Value
     * @param key String. Name of attribute
     * @param value Object. value of attribute
     * @param objectResult ObjectResult. Object Result to which it belongs
     */
    public Attribute(String key, Object value, ObjectResult objectResult) {
        this.key = key;
        this.objectResult = objectResult;
        this.values = new HashSet<>();
        if (value instanceof List) {
            for (Object v : (List) value){
                this.values.add(new Value(this,v));
            }
        } else { // No Es una lista
            this.values.add(new Value(this,value));
        }
    }


    /**
     * Equals
     * @param o Obejct. Other Attribute entity
     * @return boolean. True if are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return Objects.equals(key, attribute.key);
    }

    /**
     * hasCode method
     * @return int with hashCode
     */
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
