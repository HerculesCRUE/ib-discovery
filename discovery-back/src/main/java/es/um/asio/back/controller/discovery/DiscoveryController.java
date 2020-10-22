package es.um.asio.back.controller.discovery;

//import es.um.asio.service.model.Role;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.validation.group.Create;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Message controller.
 */
@RestController
@RequestMapping(DiscoveryController.Mappings.BASE)
public class DiscoveryController {


    @Autowired
    ApplicationState applicationState;

    @Autowired
    CacheServiceImp cache;

    /**
     * Status.
     *
     * @return Get the App status
     */
    @GetMapping()
    //@Secured(Role.ANONYMOUS_ROLE)
    public ApplicationState status() {
        return applicationState;
    }


    /**
     * Get Entity Stats.
     *
     * @return Get the Entty Stats
     */
    @GetMapping(Mappings.STATS)
    //@Secured(Role.ANONYMOUS_ROLE)
    public Map<String, Object> getEntityStats(
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = false)
            @RequestParam(required = false, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "trellis", defaultValue = "trellis", required = false)
            @RequestParam(required = false, defaultValue = "trellis") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = false)
            @RequestParam(required = true) @Validated(Create.class) final String className
    ) {
        Map<String,Object> stats = new HashMap<>();
        stats.put("status", applicationState);
        stats.put("stats",cache.getStatsHandler().buildStats(node,tripleStore,className));
        return stats;
    }

    /**
     * Mappgins.
     */
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    static final class Mappings {
        /**
         * Controller request mapping.
         */
        protected static final String BASE = "/discovery";

        protected static final String STATUS = "/status";

        protected static final String STATS = "/stats";

    }
}
