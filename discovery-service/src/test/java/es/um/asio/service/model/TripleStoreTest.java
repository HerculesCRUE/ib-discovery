package es.um.asio.service.model;

import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.util.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDiscoveryApplication.class)
@ExtendWith(SpringExtension.class)
class TripleStoreTest {

    TripleStore tripleStore;

    @PostConstruct
    public void init() {
        tripleStore = new TripleStore("trellis","um","http://localhost:8080/trellis","usr","pwd");
    }

    @Test
    void getId() {
        Assert.assertNull(tripleStore.getId());
    }

    @Test
    void getTripleStore() {
        Assert.assertTrue(tripleStore.getTripleStore().equals("trellis"));
    }

    @Test
    void getNode() {
        Node node = new Node("um");
        Assert.assertTrue(tripleStore.getNode().equals(node));
    }

    @Test
    void getBaseURL() {
        Assert.assertTrue(tripleStore.getBaseURL().equals("http://localhost:8080/trellis"));
    }

    @Test
    void getUser() {
        Assert.assertTrue(tripleStore.getUser().equals("usr"));
    }

    @Test
    void getPassword() {
        Assert.assertTrue(tripleStore.getPassword().equals("pwd"));
    }

    @Test
    void testEquals() {
        TripleStore other = new TripleStore("trellis","um","http://localhost:8080/trellis","usr","pwd");
        Assert.assertTrue(tripleStore.equals(other));
    }

    @Test
    void canEqual() {
        TripleStore other = new TripleStore("trellis","um","http://localhost:8080/trellis","usr","pwd");
        Assert.assertTrue(tripleStore.canEqual(other));
    }

    @Test
    void testHashCode() {
        TripleStore other = new TripleStore("trellis","um","http://localhost:8080/trellis","usr","pwd");
        Assert.assertTrue(tripleStore.hashCode() == other.hashCode());
    }

    @Test
    void testToString() {
        Assert.assertTrue(Utils.isValidString(tripleStore.toString()));
    }
}