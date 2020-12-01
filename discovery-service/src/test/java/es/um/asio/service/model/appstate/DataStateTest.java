package es.um.asio.service.model.appstate;

import es.um.asio.service.TestDiscoveryApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class DataStateTest {

    DataState dataState;

    @PostConstruct
    private void init() {
        dataState = new DataState();
    }

    @Test
    void setState() {
        dataState.setState(State.UPLOAD_DATA);
        Assert.assertTrue(dataState.getState().equals(State.UPLOAD_DATA));
    }

    @Test
    void setLastDate() {
        Date d = new Date();
        dataState.setLastDate(d);
        Assert.assertTrue(dataState.getLastDate().equals(d));
    }

    @Test
    void getState() {
        dataState.setState(State.UPLOAD_DATA);
        Assert.assertTrue(dataState.getState().equals(State.UPLOAD_DATA));
    }

    @Test
    void getLastDate() {
        Date d = new Date();
        dataState.setLastDate(d);
        Assert.assertTrue(dataState.getLastDate().equals(d));
    }
}