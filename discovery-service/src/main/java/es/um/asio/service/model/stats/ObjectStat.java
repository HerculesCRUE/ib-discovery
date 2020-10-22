package es.um.asio.service.model.stats;

import lombok.Getter;
import lombok.Setter;
//import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public abstract class ObjectStat {

    private String name;
    private int counter;

    public abstract float getRelativeImportanceRatio();
}
