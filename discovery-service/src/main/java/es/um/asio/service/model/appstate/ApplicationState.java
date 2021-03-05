package es.um.asio.service.model.appstate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import es.um.asio.service.config.DataProperties;
import es.um.asio.service.listener.AppEvents;
import es.um.asio.service.model.relational.DiscoveryApplication;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class for model Appication State.
 * @see DiscoveryApplication
 * @see AppState
 * @see DataState
 * @see AppEvents
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Component
@Scope("singleton")
@Getter
@Setter
public class ApplicationState {

    @JsonIgnore
    private DiscoveryApplication application;
    private String name;
    private AppState appState;
    private EnumMap<DataType,DataState> states;
    private Map<String,Date> lastFilterDate;
    private int stateCode = 503;
    @JsonIgnore
    private Set<AppEvents> appEventListeners;

    /**
     * Constructor
     */
    public ApplicationState() {
        this.appEventListeners = new HashSet<>();
        application = new DiscoveryApplication("DISCOVERY LIBRARY");
        appState = AppState.UNINITIALIZED;
        stateCode = 503;
        states = new EnumMap<>(DataType.class);
        lastFilterDate = new HashMap<>();
        states.put(DataType.CACHE, new DataState());
        states.put(DataType.ELASTICSEARCH, new DataState());
        states.put(DataType.REDIS, new DataState());
    }

    /**
     * Add listener for events
     * @see AppEvents
     * @param appEvents AppEvents. The listener
     */
    public void addAppListener(AppEvents appEvents) {
        appEventListeners.add(appEvents);
    }

    /**
     * Remove listener for events
     * @see AppEvents
     * @param appEvents AppEvents. The listener
     */
    public void removeAppListener(AppEvents appEvents) {
        appEventListeners.remove(appEvents);
    }

    /**
     * Get the DataState from DataType
     * @see DataType
     * @see DataState
     * @param dataType DataType
     * @return DataState. Get the DataState from DataType
     */
    public DataState getDataState(DataType dataType){
        return states.get(dataType);
    }


    /**
     * Change the DataState by DataType
     * @see DataType
     * @see State
     * @param dataType DataType. Data type to update state
     * @param state State. State that will be change
     */
    public void setDataState(DataType dataType,State state) {
        if (!states.containsKey(dataType) || state.compareTo(states.get(dataType).getState())>=0) {// Si no existía o el estado es mas actual
            states.put(dataType, new DataState(state));
            propagueEvents(dataType,state);
        }
    }

    /**
     * Propague event to listener
     * @param dataType DataType. DataType for listener
     * @param state State. State for listener
     */
    private void propagueEvents(DataType dataType,State state) {
        if (dataType == DataType.REDIS && state.getOrder() == 1) {
            for (AppEvents listener :appEventListeners) {
                listener.onCachedDataIsReady();
            }
        } else if (dataType == DataType.CACHE && state.getOrder() == 2) {
            for (AppEvents listener :appEventListeners) {
                listener.onRealDataIsReady();
            }
        } else if (dataType == DataType.ELASTICSEARCH && state.getOrder() == 2) {
            for (AppEvents listener :appEventListeners) {
                listener.onElasticSearchIsReady();
            }
        }
    }


    /**
     * Get the last filter date
     * @param className String class name to search
     * @return Date. The last Date
     */
    public Date getLastFilterDate(String className) {
        return this.lastFilterDate.containsKey(className)?this.lastFilterDate.get(className):new Date(0L);
    }

    /**
     * Update the last filter date
     * @param className String class name to search
     * @param lastFilterDate Date. The last Date
     */
    public void setLastFilterDate(String className, Date lastFilterDate) {
        this.lastFilterDate.put(className,lastFilterDate);
    }

    /**
     * Update the last filter date to current date
     * @param className String class name to search
     */
    public void setLastFilterDate(String className) {
        this.lastFilterDate.put(className,new Date());
    }

    /**
     * Update the AppState
     * @param appState AppState. The app state
     */
    public void setAppState(AppState appState) {
        if (appState.compare(this.appState)>=0) {
            this.appState = appState;
        }
        if (appState.getOrder() > 0) {
            this.stateCode = 200;
        }
    }

    /**
     * App State of the Applciation.
     * @author  Daniel Ruiz Santamaría
     * @version 2.0
     * @since   1.0
     */
    @Getter
    public enum AppState {
        UNINITIALIZED(0),
        INITIALIZED_WITH_CACHED_DATA(1),
        INITIALIZED(2);

        private int order;

        /**
         * Constructor
         * @param order Int. The order
         */
        AppState(int order) {
            this.order = order;
        }

        /**
         * Comparator of AppState
         * @param other AppState. Other AppState
         * @return -1 if si less, 0 if is equal, 1 if is greater
         */
        public int compare(AppState other) {
            return this.getOrder() - other.getOrder();
        }
    }

    /**
     * Build Json from attributes
     * @return JsonObject. The Json
     */
    public JsonObject toSimplifiedJson() {
        JsonObject jState = new JsonObject();
        jState.addProperty("appState",getAppState().toString());
        jState.addProperty("cacheState",getDataState(DataType.REDIS).getState().toString());
        jState.addProperty("dataState",getDataState(DataType.CACHE).getState().toString());
        jState.addProperty("elasticState",getDataState(DataType.ELASTICSEARCH).getState().toString());
        return jState;
    }
}
