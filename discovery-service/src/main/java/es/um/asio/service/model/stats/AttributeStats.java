package es.um.asio.service.model.stats;

import es.um.asio.service.model.relational.Value;
import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * AttributeStats Class. Model the stats of attributes in entities. All the values are inserted for calculate stats
 * @see Value
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeStats extends ObjectStat{

    Set<Object> values;
    private int counter;

    /**
     * Constructor
     * @param name String. The attribute name
     */
    public AttributeStats(String name) {
        setName(name);
        setCounter(0);
        values = new HashSet<>();
    }

    /**
     * Constructor
     * @param name String. The attribute name
     * @param value String. The attribute value
     */
    public AttributeStats(String name, Object value) {
        this(name);
        addValue(value);
    }

    /**
     * Add value in attribute
     * @param value Object. The value of the attribute
     */
    public void addValue(Object value) {
        if (value!=null && (!(value instanceof String) || Utils.isValidString(value.toString()))) {
            setCounter(getCounter() + 1);
            getValues().add(value);
        }
    }

    /**
     * Calculate the importance ratio by the attribute
     * @return float. The relevance Ratio
     */
    @Override
    public float getRelativeImportanceRatio() {
        return Float.valueOf(getValues().size())/Float.valueOf(getCounter());
    }


    /**
     * Equals
     * @param o Object. AttributeStats
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeStats that = (AttributeStats) o;
        return Objects.equals(getName(), that.getName());
    }

    /**
     * hashCode
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
