package es.um.asio.service.model.appstate;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
@Getter
@Setter
public class ApplicationState {

    private AppState appState;
    private Map<DataType,DataState> states;
    private Map<String,Date> lastFilterDate;

    public ApplicationState() {
        appState = AppState.UNINITIALIZED;
        states = new HashMap<>();
        lastFilterDate = new HashMap<>();
        states.put(DataType.CACHE, new DataState());
        states.put(DataType.ELASTICSEARCH, new DataState());
        states.put(DataType.REDIS, new DataState());
    }

    public DataState getDataState(DataType dataType){
        return states.get(dataType);
    }

    public void setDataState(DataType dataType,State state, Date lastUpdate) {
        if (!states.containsKey(dataType) || state.compareTo(states.get(dataType).getState())>=0) // Si no existía o el estado es mas actual
            states.put(dataType, new DataState(state,lastUpdate));
    }

    public void setDataState(DataType dataType,State state) {
        if (!states.containsKey(dataType) || state.compareTo(states.get(dataType).getState())>=0) // Si no existía o el estado es mas actual
            states.put(dataType, new DataState(state));
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
    }

    public JsonObject buildAppState(){
        JsonObject state = new JsonObject();
        return state;
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
}
