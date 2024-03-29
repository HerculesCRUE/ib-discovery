package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.service.CacheService;
import es.um.asio.service.util.Utils;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JobRegistry Class. In relational model the Job Registry entity. Job registry is a Job of search of similarities.
 * @see DiscoveryApplication
 * @see RequestRegistry
 * @see StatusResult
 * @see ObjectResult
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Entity
@Table(name = JobRegistry.TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class JobRegistry implements Cloneable {

    public static final String TABLE = "job_registry";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = DiscoveryApplication.Columns.ID,columnDefinition = "CHAR(32)",updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private String id;

    @Version
    private Long version;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private DiscoveryApplication discoveryApplication;


    @Column(name = Columns.NODE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String node;

    @Column(name = Columns.TRIPLE_STORE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String tripleStore;

    @Column(name = Columns.CLASS_NAME, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    private String className;

    @Column(name = Columns.DATA_SOURCE, nullable = true,columnDefinition = "VARCHAR(200)",length = 200)
    private String dataSource;

    @OneToMany( mappedBy = "jobRegistry", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<RequestRegistry> requestRegistries;

    @Column(name = Columns.COMPLETION_DATE, nullable = true,columnDefinition = "DATETIME")
    private Date completedDate;

    @Column(name = Columns.STARTED_DATE, nullable = true,columnDefinition = "DATETIME")
    private Date startedDate;

    @Column(name = Columns.STATUS_RESULT, nullable = false,columnDefinition = "VARCHAR(40) DEFAULT 'PENDING'",length = 40)
    @Enumerated(value = EnumType.STRING)
    private StatusResult statusResult;

    @Column(name = Columns.IS_COMPLETED, nullable = false)
    private boolean isCompleted = false;

    @Column(name = Columns.IS_STARTED, nullable = false)
    private boolean isStarted = false;

    @Column(name = Columns.DO_SYNCHRONOUS, nullable = false)
    private boolean doSync = false;

    @Column(name = Columns.SEARCH_LINKS, nullable = false)
    private boolean searchLinks = false;

    @Column(name = Columns.SEARCH_FROM_DELTA, nullable = true)
    private Date searchFromDelta;

    @Column(name = Columns.BODY_REQUEST, nullable = true,columnDefinition = "VARCHAR(4000)")
    private String bodyRequest;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobRegistry", cascade = CascadeType.DETACH)
    private Set<ObjectResult> objectResults;

    @Transient
    @JsonIgnore
    private TripleObject tripleObject;


    /**
     * Constructor
     * @see DiscoveryApplication
     * @param discoveryApplication DiscoveryApplication. The discovery application
     * @param node String. The node name
     * @param tripleStore String. The triple Store name
     * @param className String. The class name
     * @param searchLinks. boolean. Is true then search links in other nodes
     */
    public JobRegistry(DiscoveryApplication discoveryApplication, String node, String tripleStore, String className, boolean searchLinks) {
        this.discoveryApplication = discoveryApplication;
        this.node = node;
        this.tripleStore = tripleStore;
        this.className = className;
        this.searchLinks = searchLinks;
        this.statusResult = StatusResult.PENDING;
        this.startedDate = new Date();
        this.requestRegistries = new HashSet<>();
        this.objectResults = new HashSet<>();
    }

    public JobRegistry(JobRegistry jr) {
        this.id = jr.getId();
        this.version = jr.getVersion();
        this.discoveryApplication = jr.getDiscoveryApplication();
        this.node = jr.getNode();
        this.tripleStore = jr.getTripleStore();
        this.className = jr.getClassName();
        this.dataSource = jr.getDataSource();
        this.requestRegistries = jr.getRequestRegistries();
        this.completedDate = jr.getCompletedDate();
        this.startedDate = jr.getStartedDate();
        this.statusResult = jr.getStatusResult();
        this.isCompleted = jr.isCompleted();
        this.isStarted = jr.isStarted();
        this.doSync = jr.isDoSync();
        this.searchLinks = jr.isSearchLinks();
        this.searchFromDelta = jr.getSearchFromDelta();
        this.bodyRequest = jr.getBodyRequest();
        setObjectResults(jr.getObjectResults());
        this.tripleObject = jr.getTripleObject();
    }

    public JobRegistry(Tuple t) {
        this.id = (t.get("jr_id")!=null)?t.get("jr_id").toString():null;
        this.bodyRequest = (t.get("jr_body_request")!=null)?t.get("jr_body_request").toString():null;
        this.className = (t.get("jr_class_name")!=null)?t.get("jr_class_name").toString():null;
        this.completedDate = (t.get("jr_completion_date")!=null)?((Date)t.get("jr_completion_date")):null;
        this.dataSource = (t.get("jr_data_source")!=null)?((String)t.get("jr_data_source")):null;
        this.doSync = (t.get("jr_do_synchronous")!=null)?((boolean)t.get("jr_do_synchronous")):null;
        this.isCompleted = (t.get("jr_is_completed")!=null)?((boolean)t.get("jr_is_completed")):null;
        this.isStarted = (t.get("jr_is_started")!=null)?((boolean)t.get("jr_is_started")):null;
        this.node = (t.get("jr_node")!=null)?((String)t.get("jr_node")):null;
        this.searchFromDelta = (t.get("jr_search_from_delta")!=null)?((Date)t.get("jr_search_from_delta")):null;
        this.searchLinks = (t.get("jr_search_links")!=null)?((boolean)t.get("jr_search_links")):null;
        this.startedDate = (t.get("jr_started_date")!=null)?((Date)t.get("jr_started_date")):null;
        this.statusResult = (t.get("jr_status_result")!=null)?(StatusResult.getFromString(t.get("jr_status_result").toString())):null;
        this.tripleStore = (t.get("jr_triple_store")!=null)?((String)t.get("jr_triple_store")):null;
        this.version = (long) ((t.get("jr_version")!=null)?(Long.valueOf(t.get("jr_version").toString())):null);
        this.requestRegistries = new HashSet<>();
        this.objectResults = new HashSet<>();
    }

    public void copy(JobRegistry jr) {
        this.id = jr.getId();
        this.version = jr.getVersion();
        this.discoveryApplication = jr.getDiscoveryApplication();
        this.node = jr.getNode();
        this.tripleStore = jr.getTripleStore();
        this.className = jr.getClassName();
        this.dataSource = jr.getDataSource();
        this.requestRegistries = jr.getRequestRegistries();
        this.completedDate = jr.getCompletedDate();
        this.startedDate = jr.getStartedDate();
        this.statusResult = jr.getStatusResult();
        this.isCompleted = jr.isCompleted();
        this.isStarted = jr.isStarted();
        this.doSync = jr.isDoSync();
        this.searchLinks = jr.isSearchLinks();
        this.searchFromDelta = jr.getSearchFromDelta();
        this.bodyRequest = jr.getBodyRequest();
        this.setObjectResults(jr.getObjectResults());
        this.tripleObject = jr.getTripleObject();
    }

    /**
     * Get the last date of the more similar JobRegistry
     * @return Date. The date of the more similar JobRegistry if exist, else null
     */
    public Date getMaxRequestDate() {
        if (requestRegistries == null || requestRegistries.isEmpty())
            return new Date(Long.MIN_VALUE);
        else {
            Date max = new Date(Long.MIN_VALUE);
            for (RequestRegistry requestRegistry : requestRegistries) {
                if (requestRegistry.getRequestDate().after(max))
                    max = requestRegistry.getRequestDate();
            }
            return max;
        }
    }

    /**
     * @see RequestRegistry
     * @param requestRegistry RequestRegistry. Add a new RequestRegistry to Job
     */
    public void addRequestRegistry(RequestRegistry requestRegistry) {
        requestRegistry.setJobRegistry(this);
        this.requestRegistries.remove(requestRegistry);
        this.requestRegistries.add(requestRegistry);
    }

    /**
     * Get the value of started date attribute in String
     * @return String. The value of started date attribute in String
     */
    public String getStarDateStr() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getStartedDate());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the value of completed date attribute in String
     * @return String. The value of completed date attribute in String
     */
    public String getCompletedDateStr() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getCompletedDate());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Build Job Registry in Json (Simplified)
     * @return JsonObject
     */
    public JsonObject toSimplifiedJson(CacheService cacheService) {
        JsonObject jResponse = new JsonObject();
        jResponse.addProperty("node",getNode());
        jResponse.addProperty("tripleStore", getTripleStore());
        jResponse.addProperty("className", getClassName());
        jResponse.addProperty("startDate", getStarDateStr());
        jResponse.addProperty("endDate", getCompletedDateStr());
        jResponse.addProperty("status", getStatusResult().toString());

        JsonArray jResultsArray = new JsonArray();
        for (ObjectResult or : orderObjectsResult()) {
            jResultsArray.add(or.toSimplifiedJson(true,cacheService));
        }
        jResponse.add("results",jResultsArray);
        return jResponse;
    }

    /**
     * get the Web Hooks in the Requests
     * @return Set<String>. The Web Hooks
     */
    public Set<String> getWebHooks() {
        Set<String> webHooks = new HashSet<>();
        for (RequestRegistry rr : requestRegistries) {
            if (Utils.isValidString(rr.getWebHook())){
                webHooks.add(rr.getWebHook());
            }
        }
        return webHooks;
    }

    /**
     * get the Web Hooks in the Requests
     * @return Set<String>. The Web Hooks
     */
    public Set<String> getMails() {
        Set<String> mails = new HashSet<>();
        for (RequestRegistry rr : requestRegistries) {
            if (Utils.isValidEmailAddress(rr.getEmail())){
                mails.add(rr.getEmail());
            }
        }
        return mails;
    }

    /**
     * get true if propague in kafka is true the Requests
     * @return boolean. True if propague in kafka is true the Requests
     */
    public boolean isPropagatedInKafka() {
        for (RequestRegistry rr : requestRegistries) {
            if(rr.isPropagueInKafka())
                return true;
        }
        return false;
    }

    public List<ObjectResult> orderObjectsResult() {
        List<ObjectResult> objectResultAux = new ArrayList<>(this.objectResults);
        Collections.sort(objectResultAux,Collections.reverseOrder());
        return objectResultAux;
    }

    public List<String> requestTypes(){
        List<String> rts = new ArrayList<>();
        for ( RequestRegistry rr : requestRegistries ) {
            rts.add(rr.getRequestType().toString());
        }
        return rts;
    }

    public Map<String,Integer> getStats() {
        Map<String,Integer> similaritiesStats = new HashMap<>();
        similaritiesStats.put("MANUAL",0);
        similaritiesStats.put("AUTOMATIC",0);
        similaritiesStats.put("LINK",0);
        similaritiesStats.put("ACTION-TOTAL",0);
        similaritiesStats.put("ACTION-INSERT",0);
        similaritiesStats.put("ACTION-UPDATE",0);
        similaritiesStats.put("ACTION-DELETE",0);
        similaritiesStats.put("ACTION-LINK",0);
        similaritiesStats.put("ACTION-LODLINK",0);
        for (ObjectResult or : this.getObjectResults()) {
            similaritiesStats.put("MANUAL",similaritiesStats.get("MANUAL")+or.getManual().size());
            similaritiesStats.put("AUTOMATIC",similaritiesStats.get("AUTOMATIC")+or.getAutomatic().size());
            similaritiesStats.put("LINK",similaritiesStats.get("LINK")+or.getLink().size());
            for (ActionResult ar : or.getActionResults()) {
                Action action = ar.getAction();
                if (action.equals(Action.INSERT)) {
                    similaritiesStats.put("ACTION-INSERT", similaritiesStats.get("ACTION-INSERT") + ar.getObjectResults().size());
                } else if (action.equals(Action.UPDATE)) {
                    similaritiesStats.put("ACTION-UPDATE", similaritiesStats.get("ACTION-UPDATE") + ar.getObjectResults().size());
                } else if (action.equals(Action.DELETE)) {
                    similaritiesStats.put("ACTION-DELETE", similaritiesStats.get("ACTION-DELETE") + ar.getObjectResults().size());
                } else if (action.equals(Action.LINK)) {
                    similaritiesStats.put("ACTION-LINK", similaritiesStats.get("ACTION-LINK") + ar.getObjectResults().size());
                } else if (action.equals(Action.LOD_LINK)) {
                    similaritiesStats.put("ACTION-LODLINK", similaritiesStats.get("ACTION-LODLINK") + ar.getObjectResults().size());
                }
                similaritiesStats.put("ACTION-TOTAL", similaritiesStats.get("ACTION-TOTAL") + ar.getObjectResults().size());
            }
        }
        return similaritiesStats;
    }

    /**
     * Finalize object
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public JobRegistry clone() throws CloneNotSupportedException {
        return (JobRegistry) super.clone();
    }

    /**
     * Column name constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Columns {
        /**
         * ID column.
         */
        protected static final String ID = "id";
        /**
         * NODE column.
         */
        protected static final String NODE = "node";
        /**
         * TRIPLE_STORE column.
         */
        protected static final String TRIPLE_STORE = "triple_store";
        /**
         * CLASS_NAME column.
         */
        protected static final String CLASS_NAME = "class_name";
        /**
         * DATA_SOURCE column.
         */
        protected static final String DATA_SOURCE = "data_source";
        /**
         * COMPLETION_DATE column.
         */
        protected static final String COMPLETION_DATE = "completion_date";
        /**
         * STATUS_RESULT column.
         */
        protected static final String STATUS_RESULT = "status_result";
        /**
         * IS_COMPLETED column.
         */
        protected static final String IS_COMPLETED = "is_completed";
        /**
         * BODY_REQUEST column.
         */
        protected static final String BODY_REQUEST = "body_request";
        /**
         * IS_STARTED column.
         */
        protected static final String IS_STARTED = "is_started";
        /**
         * STARTED_DATE column.
         */
        protected static final String STARTED_DATE = "started_date";
        /**
         * STARTED_DATE column.
         */
        protected static final String DO_SYNCHRONOUS = "do_synchronous";
        /**
         * STARTED_DATE column.
         */
        protected static final String SEARCH_LINKS = "search_links";
        /**
         * STARTED_DATE column.
         */
        protected static final String SEARCH_FROM_DELTA = "search_from_delta";
    }

}
