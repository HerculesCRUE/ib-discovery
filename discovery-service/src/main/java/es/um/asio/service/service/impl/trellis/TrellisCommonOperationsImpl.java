package es.um.asio.service.service.impl.trellis;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ConnectionConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.specification.RequestSpecification;
import es.um.asio.service.service.trellis.TrellisCommonOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class TrellisCommonOperationsImpl implements TrellisCommonOperations {

    /** The authentication enabled. */
    @Value("${app.trellis.authentication.enabled:true}")
    private Boolean authenticationEnabled;

    /** The username. */
    @Value("${app.trellis.authentication.username}")
    private String username;

    /** The password. */
    @Value("${app.trellis.authentication.password}")
    private String password;

    @Override
    public RequestSpecification createRequestSpecification() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.config(RestAssuredConfig.config().connectionConfig(new ConnectionConfig().closeIdleConnectionsAfterEachResponse()));
        if(Boolean.TRUE.equals(authenticationEnabled)) {
            requestSpecification.header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        }
        return requestSpecification;
    }
}
