package es.um.asio.service.exceptions;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomDiscoveryExceptionTest {

    @Test
    public void whenCustomException_is_instace_of_CustomDiscoveryException(){
        Exception e = assertThrows(CustomDiscoveryException.class, () ->{
            throw new CustomDiscoveryException();
        });
        Assert.assertTrue(e instanceof CustomDiscoveryException);
    }

    @Test
    public void whenCustomException_message_is_recovery(){
        Exception e = assertThrows(CustomDiscoveryException.class, () ->{
            throw new CustomDiscoveryException("fail");
        });
        Assert.assertTrue(e.getMessage().equals("fail"));
    }

    @Test
    public void whenCustomException_message_and_cause_is_recovery(){
        Exception e = assertThrows(CustomDiscoveryException.class, () ->{
            throw new CustomDiscoveryException("fail", new Throwable());
        });
        Assert.assertTrue(e.getMessage().equals("fail"));
        Assert.assertNotNull(e.getSuppressed());
    }
}