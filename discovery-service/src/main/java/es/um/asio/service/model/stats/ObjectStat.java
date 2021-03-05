package es.um.asio.service.model.stats;

import es.um.asio.service.model.relational.Value;
import lombok.Getter;
import lombok.Setter;

/**
 * ObjectStat Class is abstract. Model the stats of attributes in entities. All the values are inserted for calculate stats
 * @see Value
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
public abstract class ObjectStat {

    private String name;
    private int counter;

    /**
     * Calculate importance ratio for Entities
     * @return float
     */
    public abstract float getRelativeImportanceRatio();
}
