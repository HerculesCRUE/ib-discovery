package es.um.asio.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TripleObject {

    @Expose(serialize = false, deserialize = false)
    private final Logger logger = LoggerFactory.getLogger(TripleObject.class);

    @Expose(serialize = true, deserialize = true)
    private String className;
    @Expose(serialize = true, deserialize = true)
    private String id;
    @Expose(serialize = true, deserialize = true)
    private Date lastModification;
    @Expose(serialize = true, deserialize = true)
    private TripleStore tripleStore;
    @Expose(serialize = true, deserialize = true)
    private LinkedTreeMap<String,Object> attributes;
}
