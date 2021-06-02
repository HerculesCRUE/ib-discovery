package es.um.asio.service.model.appstate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Data State.
 * @see State
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class DataState {

    private State state;
    private Date lastDate;
    @JsonIgnore
    private Map<String,Map<String, Map<String,Integer>>> dataStats;
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(DataState.class);

    /**
     * Constructor by default. State is NOT_INITIALIZED
     */
    public DataState() {
        state = State.NOT_INITIALIZED;
        lastDate = null;
        dataStats = new HashMap<>();
    }

    /**
     * Constructor
     * @param state. State. The state
     */
    public DataState(State state) {
        this.state = state;
        lastDate = new Date();
        dataStats = new HashMap<>();
    }

    /**
     * Add data stats by node, triple store and class name
     * @param node String. The node
     * @param tripleStore String. The triple store
     * @param className String. The class name
     * @param number String. The stat
     */
    public void addDataStats(String node, String tripleStore, String className,int number) {
        try {
            if (!dataStats.containsKey(node))
                dataStats.put(node, new HashMap<>());
            if (!dataStats.get(node).containsKey(tripleStore))
                dataStats.get(node).put(tripleStore, new HashMap<>());
            dataStats.get(node).get(tripleStore).put(className, number);
        } catch (Exception e) {
            logger.error("Error in create data stats: {}",e.getMessage());
        }
    }

}
