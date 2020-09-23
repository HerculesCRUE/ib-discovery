package es.um.asio.service.service.impl;

import es.um.asio.service.test.TestApplication;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
class FirebaseStorageStrategyTest {

    @Autowired
    FirebaseStorageStrategy firebaseStorageStrategy;


    @Test
    void readFileFromStorage() {
        String str = firebaseStorageStrategy.readFileFromStorage("test.txt");
        Assert.assertTrue(str.trim().equals("Hola Mundo"));
    }

    @Test
    void writeFile() {
        String content = "Hola Mundo";
        firebaseStorageStrategy.writeFile("test.txt","Hola Mundo");
        Assert.assertTrue(content.equals(firebaseStorageStrategy.readFileFromStorage("test.txt").trim()));
    }
}