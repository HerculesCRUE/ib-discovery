package es.um.asio.back.controller.discovery;

//import es.um.asio.service.model.Role;

import com.google.gson.JsonObject;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.appstate.DataType;
import es.um.asio.service.model.appstate.State;
import es.um.asio.service.model.relational.ActionResult;
import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.ObjectResult;
import es.um.asio.service.service.EntitiesHandlerService;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.service.impl.JobHandlerServiceImp;
import es.um.asio.service.validation.group.Create;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ExampleProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.*;

//import org.springframework.security.access.annotation.Secured;

/**
 * Message controller.
 */
@RestController
@ApiIgnore
@RequestMapping(ResultController.Mappings.BASE)
public class ResultController {


    /**
     * Status.
     *
     * @return Get the App status
     */
    @PostMapping(Mappings.RESULT)
    public Object getResponse(@RequestBody Object response) {
        return response;
    }




    /**
     * Mappgins.
     */
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    static final class Mappings {
        /**
         * Controller request mapping.
         */
        protected static final String BASE = "/discovery/";

        protected static final String RESULT = "/result";



    }
}
