package es.um.asio.service.service.impl;

import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.appstate.DataState;
import es.um.asio.service.model.appstate.DataType;
import es.um.asio.service.model.appstate.State;
import es.um.asio.service.test.TestApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
class DataHandlerImpTest {

    @Autowired
    DataHandlerImp dataHandler;

    @Autowired
    ApplicationState appState;

    @Test
    void populateData() {
        try {
            CompletableFuture<Boolean> future = dataHandler.populateData();
            boolean done = future.join();
            CompletableFuture.allOf(future);
            appState.getAppState();
            Assert.assertTrue(appState.getAppState().equals(ApplicationState.AppState.INITIALIZED));
            Assert.assertTrue(appState.getDataState(DataType.CACHE).getState().equals(State.UPLOAD_DATA));
            Assert.assertTrue(appState.getDataState(DataType.REDIS).getState().equals(State.UPLOAD_DATA));
            Assert.assertTrue(appState.getDataState(DataType.ELASTICSEARCH).getState().equals(State.UPLOAD_DATA));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}