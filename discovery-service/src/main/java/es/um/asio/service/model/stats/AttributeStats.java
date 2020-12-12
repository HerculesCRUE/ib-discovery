package es.um.asio.service.model.stats;

import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeStats extends ObjectStat{

    Set<Object> values;

    public AttributeStats(String name) {
        setName(name);
        setCounter(0);
        values = new HashSet<>();
    }

    public AttributeStats(String name, Object value) {
        this(name);
        addValue(value);
    }

    public void addValue(Object value) {
        if (value!=null && (!(value instanceof String) || Utils.isValidString(value.toString()))) {
            setCounter(getCounter() + 1);
            getValues().add(value);
        }
    }

    @Override
    public float getRelativeImportanceRatio() {
        return Float.valueOf(getValues().size())/Float.valueOf(getCounter());
    }


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

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
