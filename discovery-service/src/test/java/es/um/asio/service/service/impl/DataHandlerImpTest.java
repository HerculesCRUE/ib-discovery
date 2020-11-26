package es.um.asio.service.service.impl;

import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.appstate.DataState;
import es.um.asio.service.model.appstate.DataType;
import es.um.asio.service.model.appstate.State;
import es.um.asio.service.model.relational.DiscoveryApplication;
import es.um.asio.service.test.TestApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/*@RunWith(SpringRunner.class)
*//*@SpringBootTest(classes={main.TestApplication.class})*//*
@EnableAutoConfiguration*/
/*@RunWith(SpringRunner.class)*/
/*@SpringBootTest(classes={main.TestApplication.class})*/
// @ContextConfiguration(loader= AnnotationConfigContextLoader.class, classes = { main.TestApplication.class })
/*@Import({DataHandlerImp.class, ApplicationState.class})*/
/*@SpringBootTest(classes = { main.TestApplication.class})*/
/*@RunWith(SpringRunner.class)
@ActiveProfiles("unit-test")*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes={main.TestApplication.class})
class DataHandlerImpTest {

    @Autowired
    DataHandlerImp dataHandler;

    @Autowired
    ApplicationState appState ;

/*    @TestConfiguration
    static class DataHandlerConfiguration {
        @Bean
        public DataHandlerImp dataHandler() {
            return new DataHandlerImp();
        }

        @Bean
        public ApplicationState appState() {
            return new ApplicationState();
        }
    }*/


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