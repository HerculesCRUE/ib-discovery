package es.um.asio.back.test.controller.message;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.um.asio.back.controller.message.MessageController;
import es.um.asio.service.proxy.TriplesStorageProxy;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    /**
     * MVC test support
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Proxy service for triples. Performs DTO conversion and permission checks.
     */
    @MockBean
    private TriplesStorageProxy proxy;

    /**
     * JSON Object mapper
     */
    @Autowired
    private ObjectMapper objectMapper;
    
    @TestConfiguration
    static class UserProxyTestConfiguration {
        @Bean
        public MessageController userController() {
            return new MessageController();
        }
    }

//    @Before
//    public void setUp() throws NoSuchEntityException, TripleStoreException {
//        // Mock data
//        Mockito.doNothing().when(this.proxy).save(any(String.class));
//    }
    
//    @Test
//    public void whenInsertNewMessage_thenNoError() throws Exception {
//        
//        // @formatter:off
//
//        this.mvc.perform(post("/message")
//                .content("{\"id\":8,\"type\":\"researcher\",\"data\":{\"Label_es\":\"Investigador 9\",\"Description_es\":\"Descripción Investigador 9\",\"Label_en\":\"Researcher 9\",\"Description_en\":\"Description Researcher 9\",\"Instance of:P9:wikibase-item\":\"I:Q17\",\"Instance of:P13:string\":\"01-12-1988\",\"Dirección:P10:string\":\"Direccion 9\",\"Instance of:P14:wikibase-item\":\"I:Q29\",\"Titled in:P11:wikibase-item\":\"es:Titlulo 2\",\"Random:P15:string\":98}}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk());
//
//        // @formatter:on
//    }
    
    @Test
    public void test_Infraestructure() {
        assertNotNull(mvc);
    }
}
