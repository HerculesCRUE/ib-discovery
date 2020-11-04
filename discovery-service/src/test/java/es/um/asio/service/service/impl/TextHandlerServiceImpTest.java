package es.um.asio.service.service.impl;

import es.um.asio.service.test.TestApplication;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TextHandlerServiceImpTest {

    @Autowired
    TextHandlerServiceImp textHandlerServiceImp;

    @Test
    void removeStopWords() {
        String phrase = "En un lugar de la mancha de cuyo nombre no quiero acordarme, vivia el ingenioso hidalgo don quijote de la mancha!?*";
        String cleanPhrase = textHandlerServiceImp.removeStopWords(phrase);
        System.out.println(cleanPhrase);
        Assert.assertTrue(cleanPhrase.equals("lugar mancha cuyo nombre quiero acordarme vivia ingenioso hidalgo quijote mancha"));
    }
}