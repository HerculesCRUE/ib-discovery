package es.um.asio.back.test.proxy;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import es.um.asio.back.test.TestApplication;
import es.um.asio.service.service.TriplesStorageService;

@RunWith(SpringRunner.class)
public class MessageProxyTest {


    /**
     * Triples storage service.
     */
    @MockBean
    private TriplesStorageService service;


//    @Before
//    public void setUp() throws TripleStoreException {
//        Mockito.doNothing().when(this.service).save(any(String.class));
//    }

//    @Test
//    public void whenInsertNewMessage_thenNoError() throws TripleStoreException {
//        this.proxy.save("Message 1");
//    }
}
