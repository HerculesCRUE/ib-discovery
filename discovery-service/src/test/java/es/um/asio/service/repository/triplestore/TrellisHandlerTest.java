package es.um.asio.service.repository.triplestore;

import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.Action;
import es.um.asio.service.model.TripleStore;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.test.TestApplication;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
class TrellisHandlerTest {

    @Autowired
    CacheServiceImp cacheServiceImp;

    @Autowired
    DataSourcesConfiguration dataSourcesConfiguration;

    TrellisHandler trellisHandler;


    @BeforeEach
    void setUp() {
        DataSourcesConfiguration.Node node = dataSourcesConfiguration.getNodes().get(0);
        trellisHandler = new TrellisHandler(node.getNodeName(),node.getTripleStoreByType("trellis").getBaseURL(),node.getTripleStoreByType("trellis").getUser(),node.getTripleStoreByType("trellis").getPassword());
    }

    @Test
    void updateTripleObject() throws ParseException, IOException, URISyntaxException {
        DataSourcesConfiguration.Node node = dataSourcesConfiguration.getNodes().get(0);
        boolean isChangedDelete = trellisHandler.updateTripleObject(cacheServiceImp,"um","trellis","ConvocatoriaRecursosHumanos","http://herc-iz-front-desa.atica.um.es/ConvocatoriaRecursosHumanos/http:_hercules.org_um_es-ES_rec_ConvocatoriaRecursosHumanos_01d1452a-86bf-4944-b216-385127fc7ab2",Action.DELETE);
        Assert.assertTrue(isChangedDelete);
        boolean isChangedUpdate = trellisHandler.updateTripleObject(cacheServiceImp,"um","trellis","ConvocatoriaRecursosHumanos","http://herc-iz-front-desa.atica.um.es/ConvocatoriaRecursosHumanos/http:_hercules.org_um_es-ES_rec_ConvocatoriaRecursosHumanos_01d1452a-86bf-4944-b216-385127fc7ab2",Action.UPDATE);
        Assert.assertTrue(isChangedUpdate);
    }
}