package es.um.asio.back.controller.discovery;

//import es.um.asio.service.model.Role;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.config.DataSourcesConfiguration;
import es.um.asio.service.exceptions.CustomDiscoveryException;
import es.um.asio.service.model.SimilarityResult;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.appstate.ApplicationState;
import es.um.asio.service.model.appstate.DataState;
import es.um.asio.service.model.appstate.DataType;
import es.um.asio.service.model.appstate.State;
import es.um.asio.service.model.relational.JobRegistry;
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
//import org.springframework.security.access.annotation.Secured;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

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

    @Autowired
    EntitiesHandlerService entitiesHandlerService;

    @Autowired
    DataSourcesConfiguration dataSourcesConfiguration;

    @Autowired
    JobHandlerServiceImp jobHandlerServiceImp;

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
     * Get Entity Stats.
     *
     * @return Get the Entty Stats
     */
    @GetMapping(Mappings.ENTITY_LINK)
    //@Secured(Role.ANONYMOUS_ROLE)
    public Map<String, Object>  findEntityLinkByNodeTripleStoreAndClass(
            @ApiParam(name = "userId", value = "1", defaultValue = "1", required = true)
            @RequestParam(required = false, defaultValue = "1") @Validated(Create.class) final String userId,
            @ApiParam(name = "requestCode", value = "12345", defaultValue = "12345", required = true)
            @RequestParam(required = false, defaultValue = "12345") @Validated(Create.class) final String requestCode,
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = false)
            @RequestParam(required = false, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "trellis", defaultValue = "trellis", required = false)
            @RequestParam(required = false, defaultValue = "trellis") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = false)
            @RequestParam(required = true) @Validated(Create.class) final String className
    ) {
        JobRegistry jobRegistry = jobHandlerServiceImp.addJobRegistryForClass(applicationState.getApplication(),userId,requestCode,node,tripleStore,className);
        Map<String,Object> response = new HashMap<>();

        Map<String,Object> appState = new HashMap<>();
        appState.put("appState",applicationState.getAppState());
        appState.put("cacheState",applicationState.getDataState(DataType.REDIS));
        appState.put("dataState",applicationState.getDataState(DataType.CACHE));
        appState.put("elasticState",applicationState.getDataState(DataType.ELASTICSEARCH));
        response.put("state",appState);

        Map<String,Object> jobResponse = new HashMap<>();
        jobResponse.put("id",jobRegistry.getId());
        jobResponse.put("node",jobRegistry.getNode());
        jobResponse.put("tripleStore",jobRegistry.getTripleStore());
        jobResponse.put("className",jobRegistry.getClassName());
        jobResponse.put("status",jobRegistry.getStatusResult());
        jobResponse.put("requestCode",requestCode);
        jobResponse.put("userId",userId);
        response.put("response",jobResponse);
        return response;
    }

    /**
     * Get Entity Stats.
     *
     * @return Get the Entty Stats
     */
    @PostMapping(Mappings.ENTITY_LINK_ENTITY)
    //@Secured(Role.ANONYMOUS_ROLE)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "object",
                    dataType = "TripleObject",
                    examples = @io.swagger.annotations.Example(
                            value = {
                                    @ExampleProperty(value = "’object‘：{'id': '1','className: 'Actividad','attributes':{" +
                                            "'codTipoActividad': '00',"+
                                            "'codTipoMoneda': 'EUR',"+
                                            "'fechaInicioActividad': '2008/04/28 00:00:00',"+
                                            "'idActividad': '5192',"+
                                            "'idGrupoActividades': '3470',"+
                                            "'idTercero': '146051',"+
                                            "'importeBase': '2400.0',"+
                                            "'importeRepercutido': '384.0',"+
                                            "'importeTotal': '2784.0',"+
                                            "'terceroConfidencial': 'N',"+
                                            "'titulo': 'INVESTIGACIÓN DE PLAN DE ACTUACIÓN PARA PREVENIR Y/O CONTROLAR UN BROTE DE BOTULISMO EN EL HONGO. VERANO 2008',"+
                                            "}" +
                                            "}", mediaType = "application/json")
                            }))
    })
    @ResponseBody
    public ResponseEntity findEntityLinkByEntityAndNodeTripleStoreAndClass(
            @ApiParam(name = "node", value = "um", defaultValue = "um", required = false)
            @RequestParam(required = false, defaultValue = "um") @Validated(Create.class) final String node,
            @ApiParam(name = "tripleStore", value = "trellis", defaultValue = "trellis", required = false)
            @RequestParam(required = false, defaultValue = "trellis") @Validated(Create.class) final String tripleStore,
            @ApiParam(name = "className", value = "Class Name", required = false)
            @RequestParam(required = true) @Validated(Create.class) final String className,
            @NotNull @RequestBody final Object object
    ) {
        JSONObject jsonData = new JSONObject((LinkedHashMap) object);
        TripleObject tripleObject = null;
        try {
            tripleObject = new TripleObject(node,tripleStore,className,jsonData);
        } catch (Exception e) {
            return new ResponseEntity<String>("Object data parse error",HttpStatus.NOT_ACCEPTABLE);
        }
        if (applicationState.getDataState(DataType.CACHE).getState() != State.NOT_INITIALIZED) {
            SimilarityResult similarity = entitiesHandlerService.findEntitiesLinksByNodeAndTripleStoreAndTripleObject(tripleObject);
            Map<String, Object> stats = new HashMap<>();
            stats.put("status", applicationState.getAppState());
            stats.put("similarity", similarity);
            return new ResponseEntity<Map<String, Object>>(stats,HttpStatus.FOUND);
        } else {
            Map<String, Object> stats = new HashMap<>();
            stats.put("status", applicationState);
            stats.put("similarity", null);
            return new ResponseEntity<Map<String, Object>>(stats,HttpStatus.SERVICE_UNAVAILABLE);
        }
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

        protected static final String STATUS = "/status";

        protected static final String STATS = "/stats";

        protected static final String ENTITY_LINK = "/entity-link";

        protected static final String ENTITY_LINK_ENTITY = "/entity-link/entity";

    }
}
