package es.um.asio.service.model;

import data.DataGenerator;
import es.um.asio.service.TestDiscoveryApplication;
import es.um.asio.service.util.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
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
class NodeTest {

    List<Node> nodes;

    @BeforeEach
    public void setUp() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        nodes = dataGenerator.getNodes();
    }

    @Test
    void getNode() {
        for (Node node : nodes) {
            Assert.assertTrue(Utils.isValidString(node.getNode()));
        }
    }

    @Test
    void testEquals() {
        for (Node node : nodes) {
            for (Node nodeInner : nodes) {
                if (node.getNode().equals(nodeInner.getNode()))
                    Assert.assertTrue(node.equals(nodeInner));
                else
                    Assert.assertFalse(node.equals(nodeInner));
            }
        }
    }

    @Test
    void canEqual() {
        for (Node node : nodes) {
            for (Node nodeInner : nodes) {
                Assert.assertTrue(node.canEqual(nodeInner));
            }
        }
    }

    @Test
    void testHashCode() {
        for (Node node : nodes) {
            for (Node nodeInner : nodes) {
                if (node.getNode().equals(nodeInner.getNode()))
                    Assert.assertTrue(node.hashCode() == nodeInner.hashCode());
                else
                    Assert.assertFalse(node.hashCode() == nodeInner.hashCode());
            }
        }
    }

    @Test
    void testToString() {
        for (Node node : nodes) {
            Assert.assertTrue(Utils.isValidString(node.toString()));
        }
    }
}