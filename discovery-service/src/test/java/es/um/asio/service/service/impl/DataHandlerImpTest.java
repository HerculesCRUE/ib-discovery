package es.um.asio.service.service.impl;

import es.um.asio.service.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
class DataHandlerImpTest {

    @Autowired
    DataHandlerImp dataHandler;

    @Test
    void main() {

        System.out.println(1);
        CompletableFuture<Boolean> future = dataHandler.populateData();
        System.out.println();
        CompletableFuture.allOf(future);
        System.out.println();
    }
}