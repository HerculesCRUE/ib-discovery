package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.util.Utils;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = Value.TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Value {

    public static final String TABLE = "value";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @EqualsAndHashCode.Include
    private Attribute attribute;

    @Column(name = Columns.TYPE, nullable = false)
    @Enumerated(value = EnumType.STRING)
    @EqualsAndHashCode.Include
    private DataType dataType;

    @Column(name = Columns.VALUE, nullable = true,columnDefinition = "TEXT")
    @EqualsAndHashCode.Include
    private String value;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentValue", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Include
    private Set<Attribute> attributes;

    public Value(Attribute attribute, Object value) {
        this.attribute = attribute;
        this.dataType = getDataType(value);
        this.attributes = new HashSet<>();
        if (dataType == DataType.OBJECT) {
            LinkedTreeMap<String,Object> attrs = (LinkedTreeMap) value;
            for (Map.Entry<String,Object> vEntry: attrs.entrySet()) {
                Attribute at = new Attribute(vEntry.getKey(),vEntry.getValue(),null);
                at.setParentValue(this);
                this.attributes.add(at);
            }
        } else {
            this.value = String.valueOf(value);
        }

    }

    public DataType getDataType(Object v){
        if (Utils.checkIfFloat(String.valueOf(v)))
            return DataType.FLOAT;
        else if (Utils.checkIfDouble(String.valueOf(v)))
            return DataType.DOUBLE;
        else if (Utils.checkIfInt(String.valueOf(v)))
            return DataType.INTEGER;
        else if (Utils.checkIfLong(String.valueOf(v)))
            return DataType.LONG;
        else if (Utils.checkIfBoolean(String.valueOf(v)))
            return DataType.BOOLEAN;
        else if (Utils.checkIfDaten(String.valueOf(v)))
            return DataType.DATE;
        else if (Utils.isObject(String.valueOf(v)))
            return DataType.OBJECT;
        else if (Utils.checkIfString(String.valueOf(v)))
            return DataType.STRING;
        else
            return DataType.STRING;

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
        protected static final String KEY = "key";
        /**
         * TYPE column.
         */
        protected static final String TYPE = "type";

        /**
         * TYPE column.
         */
        protected static final String VALUE = "value";
    }
}
