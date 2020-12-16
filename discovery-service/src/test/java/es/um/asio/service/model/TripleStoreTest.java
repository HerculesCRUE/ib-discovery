package es.um.asio.service.model;

import es.um.asio.service.util.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
class TripleStoreTest {

    TripleStore tripleStore;

    @BeforeEach
    public void setUp() {
        tripleStore = new TripleStore("trellis","um","http://localhost:8080/trellis","usr","pwd");
    }

    @Test
    void getId() {
        Assert.assertNull(tripleStore.getId());
    }

    @Test
    void getTripleStore() {
        Assert.assertTrue(tripleStore.getName().equals("trellis"));
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