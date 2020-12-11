package es.um.asio.service.model.appstate;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
class DataStateTest {

    DataState dataState;

    @BeforeEach
    private void setUp() {
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