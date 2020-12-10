package es.um.asio.service.model.appstate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import es.um.asio.service.listener.AppEvents;
import es.um.asio.service.model.relational.DiscoveryApplication;
import es.um.asio.service.repository.relational.DiscoveryApplicationRepository;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.client.license.LicensesStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("singleton")
@Getter
@Setter
public class ApplicationState {

    @JsonIgnore
    private DiscoveryApplication application;
    private String name;
    private AppState appState;
    private Map<DataType,DataState> states;
    private Map<String,Date> lastFilterDate;
    private int stateCode = 503;
    @JsonIgnore
    private Set<AppEvents> appEventListeners;

    public ApplicationState() {
        this.appEventListeners = new HashSet<>();
        application = new DiscoveryApplication("DISCOVERY LIBRARY");
        appState = AppState.UNINITIALIZED;
        stateCode = 503;
        states = new HashMap<>();
        lastFilterDate = new HashMap<>();
        states.put(DataType.CACHE, new DataState());
        states.put(DataType.ELASTICSEARCH, new DataState());
        states.put(DataType.REDIS, new DataState());
    }

    public void addAppListener(AppEvents appEvents) {
        appEventListeners.add(appEvents);
    }

    public void removeAppListener(AppEvents appEvents) {
        appEventListeners.remove(appEvents);
    }

    public DataState getDataState(DataType dataType){
        return states.get(dataType);
    }

    public void setDataState(DataType dataType,State state, Date lastUpdate) {
        if (!states.containsKey(dataType) || state.compareTo(states.get(dataType).getState())>=0) {// Si no existía o el estado es mas actual
            states.put(dataType, new DataState(state));
            propagueEvents(dataType,state);
        }

    }

    private void propagueEvents(DataType dataType,State state) {
        if (dataType == DataType.REDIS && state.getOrder() == 2) {
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

    public void setDataState(DataType dataType,State state) {
        if (!states.containsKey(dataType) || state.compareTo(states.get(dataType).getState())>=0) {// Si no existía o el estado es mas actual
            states.put(dataType, new DataState(state));
            propagueEvents(dataType,state);
        }
    }

    public Date getLastFilterDate(String className) {
        return this.lastFilterDate.containsKey(className)?this.lastFilterDate.get(className):new Date(0L);
    }

    public void setLastFilterDate(String className, Date lastFilterDate) {
        this.lastFilterDate.put(className,lastFilterDate);
    }

    public void setLastFilterDate(String className) {
        this.lastFilterDate.put(className,new Date());
    }

    public void setAppState(AppState appState) {
        if (appState.compare(this.appState)>=0) {
            this.appState = appState;
        }
        if (appState.getOrder() > 0) {
            this.stateCode = 200;
        }
    }


    @Getter
    public enum AppState {
        UNINITIALIZED(0),
        INITIALIZED_WITH_CACHED_DATA(1),
        INITIALIZED(2);

        private int order;

        AppState(int order) {
            this.order = order;
        }

        public int compare(AppState other) {
            return this.getOrder() - other.getOrder();
        }
    }

    public JsonObject toSimplifiedJson() {
        JsonObject jState = new JsonObject();
        jState.addProperty("appState",getAppState().toString());
        jState.addProperty("cacheState",getDataState(DataType.REDIS).getState().toString());
        jState.addProperty("dataState",getDataState(DataType.CACHE).getState().toString());
        jState.addProperty("elasticState",getDataState(DataType.ELASTICSEARCH).getState().toString());
        return jState;
    }
}
