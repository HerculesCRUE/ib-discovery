package es.um.asio.service.config;

import es.um.asio.service.model.TripleObject;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Sources Configuration configuration properties.
 * @see Datasources.Thresholds
 * @see Datasources.Node
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Component
@ConfigurationProperties("hierarchy")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Hierarchies implements Cloneable {

    private List<Hierarchy> hierarchies = new ArrayList<>();

    public List<String> getParentsByClass(String className) {
        List<String> parents = new ArrayList<>();
        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.getClassName().equalsIgnoreCase(className)) {
                parents.add(hierarchy.parentClassName);
            }
        }
        return parents;
    }

    public boolean isChildClass(String className) {
        for (Hierarchy hierarchy : this.hierarchies) {
            if (hierarchy.getClassName().trim().equalsIgnoreCase(className.trim()))
                return true;
        }
        return false;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Hierarchy implements Cloneable{
        private String className;
        private String parentClassName;
    }
}
