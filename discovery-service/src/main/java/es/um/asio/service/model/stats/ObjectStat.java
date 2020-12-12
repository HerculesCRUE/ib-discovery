package es.um.asio.service.model.stats;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class ObjectStat {

    private String name;
    private int counter;

    public abstract float getRelativeImportanceRatio();
}
