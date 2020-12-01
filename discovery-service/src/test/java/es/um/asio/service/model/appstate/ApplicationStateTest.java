package es.um.asio.service.model.appstate;

import com.google.gson.JsonObject;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.listener.AppEvents;
import es.um.asio.service.model.relational.DiscoveryApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.PostConstruct;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class ApplicationStateTest {

    ApplicationState applicationState;
    AppEvents appEvents;

    @PostConstruct
    public void init(){
        applicationState = new ApplicationState();
        appEvents = new AppEvents() {
            @Override
            public void onCachedDataIsReady() {

            }

            @Override
            public void onRealDataIsReady() {

            }

            @Override
            public void onElasticSearchIsReady() {

            }
        };
        applicationState.addAppListener(appEvents);
    }

    @Test
    void addAppListener() {
        applicationState.addAppListener(appEvents);
        Assert.assertTrue(applicationState.getAppEventListeners().contains(appEvents));
    }

    @Test
    void removeAppListener() {
        applicationState.removeAppListener(appEvents);
        Assert.assertFalse(applicationState.getAppEventListeners().contains(appEvents));
    }

    @Test
    void getDataState() {
        Assert.assertTrue(applicationState.getDataState(DataType.REDIS).getState().equals(State.NOT_INITIALIZED));
        Assert.assertTrue(applicationState.getDataState(DataType.ELASTICSEARCH).getState().equals(State.NOT_INITIALIZED));
        Assert.assertTrue(applicationState.getDataState(DataType.CACHE).getState().equals(State.NOT_INITIALIZED));
    }

    @Test
    void setDataState() {
        applicationState.setDataState(DataType.REDIS,State.UPLOAD_DATA);
        applicationState.setDataState(DataType.ELASTICSEARCH,State.CACHED_DATA);
        applicationState.setDataState(DataType.CACHE,State.NOT_INITIALIZED);
        Assert.assertTrue(applicationState.getDataState(DataType.REDIS).getState().equals(State.UPLOAD_DATA));
        Assert.assertTrue(applicationState.getDataState(DataType.ELASTICSEARCH).getState().equals(State.CACHED_DATA));
        Assert.assertTrue(applicationState.getDataState(DataType.CACHE).getState().equals(State.NOT_INITIALIZED));
    }

    @Test
    void testSetDataState() {
        applicationState.setDataState(DataType.REDIS,State.UPLOAD_DATA);
        applicationState.setDataState(DataType.ELASTICSEARCH,State.CACHED_DATA);
        applicationState.setDataState(DataType.CACHE,State.NOT_INITIALIZED);
        Assert.assertTrue(applicationState.getDataState(DataType.REDIS).getState().equals(State.UPLOAD_DATA));
        Assert.assertTrue(applicationState.getDataState(DataType.ELASTICSEARCH).getState().equals(State.CACHED_DATA));
        Assert.assertTrue(applicationState.getDataState(DataType.CACHE).getState().equals(State.NOT_INITIALIZED));
    }

    @Test
    void getLastFilterDate() {
        Assert.assertTrue(applicationState.getLastFilterDate("clase1").equals(new Date(0L)));
    }

    @Test
    void setLastFilterDate() {
        Date d = new Date();
        applicationState.setLastFilterDate("clase1",d);
        Assert.assertTrue(applicationState.getLastFilterDate("clase1").equals(d));
    }

    @Test
    void testSetLastFilterDate() {
        Date d = new Date();
        applicationState.setLastFilterDate("clase1",d);
        Assert.assertTrue(applicationState.getLastFilterDate("clase1").equals(d));
    }

    @Test
    void setAppState() {
        applicationState.setAppState(ApplicationState.AppState.INITIALIZED);
        Assert.assertTrue(applicationState.getAppState().equals(ApplicationState.AppState.INITIALIZED));
    }


    @Test
    void toSimplifiedJson() {
        JsonObject jState = applicationState.toSimplifiedJson();
        Assert.assertTrue(jState.has("appState"));
        Assert.assertTrue(jState.get("appState").getAsString().equals(applicationState.getAppState().toString()));
        Assert.assertTrue(jState.has("cacheState"));
        Assert.assertTrue(jState.get("cacheState").getAsString().equals(applicationState.getDataState(DataType.REDIS).getState().toString()));
        Assert.assertTrue(jState.has("dataState"));
        Assert.assertTrue(jState.get("dataState").getAsString().equals(applicationState.getDataState(DataType.CACHE).getState().toString()));
        Assert.assertTrue(jState.has("elasticState"));
        Assert.assertTrue(jState.get("elasticState").getAsString().equals(applicationState.getDataState(DataType.ELASTICSEARCH).getState().toString()));
    }

    @Test
    void setApplication() {
        DiscoveryApplication da = new DiscoveryApplication();
        applicationState.setApplication(da);
        Assert.assertTrue(applicationState.getApplication().equals(da));
    }

    @Test
    void setName() {
        applicationState.setName("nombre");
        Assert.assertTrue(applicationState.getName().equals("nombre"));
    }

    @Test
    void setStates() {
        Map<DataType,DataState> states = new HashMap<>();
        states.put(DataType.REDIS,new DataState());
        states.put(DataType.CACHE,new DataState());
        states.put(DataType.ELASTICSEARCH,new DataState());
        applicationState.setStates(states);
        for (Map.Entry<DataType, DataState> stateEntry : states.entrySet()) {
            Assert.assertTrue(applicationState.getDataState(stateEntry.getKey()).equals(stateEntry.getValue()));
        }
    }

    @Test
    void setStateCode() {
        applicationState.setStateCode(12345);
        Assert.assertTrue(applicationState.getStateCode() == 12345);
    }

    @Test
    void setAppEventListeners() {
        HashSet<AppEvents> events = new HashSet<>();
        events.add(appEvents);
        Assert.assertTrue(applicationState.getAppEventListeners().size() == 1 && applicationState.getAppEventListeners().contains(appEvents));
    }

    @Test
    void getApplication() {
        DiscoveryApplication da = new DiscoveryApplication();
        applicationState.setApplication(da);
        Assert.assertTrue(applicationState.getApplication().equals(da));
    }

    @Test
    void getName() {
        applicationState.setName("nombre");
        Assert.assertTrue(applicationState.getName().equals("nombre"));
    }

    @Test
    void getAppState() {
        Assert.assertTrue(applicationState.getAppState().equals(ApplicationState.AppState.UNINITIALIZED));
    }

    @Test
    void getStates() {
        Map<DataType,DataState> states = new HashMap<>();
        states.put(DataType.REDIS,new DataState());
        states.put(DataType.CACHE,new DataState());
        states.put(DataType.ELASTICSEARCH,new DataState());
        applicationState.setStates(states);
        for (Map.Entry<DataType, DataState> stateEntry : applicationState.getStates().entrySet()) {
            Assert.assertTrue(states.containsKey(stateEntry.getKey()) && states.get(stateEntry.getKey()).equals(stateEntry.getValue()));
        }
    }

    @Test
    void testGetLastFilterDate() {
        Assert.assertTrue(applicationState.getLastFilterDate("clase1").equals(new Date(0L)));
    }

    @Test
    void getStateCode() {
        applicationState.setStateCode(12345);
        Assert.assertTrue(applicationState.getStateCode() == 12345);
    }

    @Test
    void getAppEventListeners() {
        Assert.assertTrue(applicationState.getAppEventListeners().size() == 1 && applicationState.getAppEventListeners().contains(appEvents));
    }
}