package es.um.asio.back.controller.discovery;

//import es.um.asio.service.model.Role;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
