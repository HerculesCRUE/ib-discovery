package es.um.asio.service.model.appstate;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
class StateTest {

    @Test
    void getOrder() {
        Assert.assertTrue(State.NOT_INITIALIZED.getOrder() == 0);
        Assert.assertTrue(State.CACHED_DATA.getOrder() == 1);
        Assert.assertTrue(State.UPLOAD_DATA.getOrder() == 2);
    }

    @Test
    void values() {

        List<State> values = Arrays.asList(State.values());
        Assert.assertTrue(values.size() == 3);
        Assert.assertTrue(values.contains(State.NOT_INITIALIZED));
        Assert.assertTrue(values.contains(State.CACHED_DATA));
        Assert.assertTrue(values.contains(State.UPLOAD_DATA));
    }

    @Test
    void valueOf() {
        List<State> values = Arrays.asList(State.values());
        for (State s : values) {
            Assert.assertTrue(State.valueOf(s.toString()).equals(s));
        }
    }
}