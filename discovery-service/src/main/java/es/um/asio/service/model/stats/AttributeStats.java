package es.um.asio.service.model.stats;

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
public class AttributeStats {
    private String attribute;
    private Set<String> values;
    private int counter;

    public AttributeStats(String attribute) {
        counter = 0;
        this.attribute = attribute;
        this.values = new HashSet<>();
    }

    public void addValue(String s) {
        counter++;
        this.values.add(s);
    }

    public float getVariety() {
        return Float.valueOf(values.size())/Float.valueOf(counter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
            AttributeStats that = (AttributeStats) o;
        return Objects.equals(attribute, that.attribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute);
    }
}
