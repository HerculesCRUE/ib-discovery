package es.um.asio.service.service.impl;

import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.repository.elasticsearch.TripleObjectESRepository;
import es.um.asio.service.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
class ElasticsearchServiceImpTest {

    @Autowired
    TripleObjectESRepository repository;

    @Test
    public void save_in_es() {
        TripleObjectES to = new TripleObjectES("1","objeto1");
        //TripleObject to = new TripleObject("1","claseTest",new Date(),ts, attrs);
        repository.save(to);
        System.out.println();

    }

}