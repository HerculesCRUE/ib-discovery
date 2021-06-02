package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.um.asio.service.model.TripleObject;
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
public class JobRegistry {

    public static final String TABLE = "job_registry";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = DiscoveryApplication.Columns.ID,columnDefinition = "CHAR(32)",updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DiscoveryApplication discoveryApplication;


    @Column(name = Columns.NODE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String node;

    @Column(name = Columns.TRIPLE_STORE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String tripleStore;

    @Column(name = Columns.CLASS_NAME, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    private String className;

    @Column(name = Columns.DATA_SOURCE, nullable = true,columnDefinition = "VARCHAR(200)",length = 200)
    private String dataSource;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobRegistry", cascade = CascadeType.ALL, orphanRemoval = true)
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

    @Column(name = Columns.BODY_REQUEST, nullable = true,columnDefinition = "TEXT")
    private String bodyRequest;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobRegistry", cascade = CascadeType.ALL)
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
        this.requestRegistries = new HashSet<>();
        this.objectResults = new HashSet<>();
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
    public JsonObject toSimplifiedJson() {
        JsonObject jResponse = new JsonObject();
        jResponse.addProperty("node",getNode());
        jResponse.addProperty("tripleStore", getTripleStore());
        jResponse.addProperty("className", getClassName());
        jResponse.addProperty("startDate", getStarDateStr());
        jResponse.addProperty("endDate", getCompletedDateStr());
        jResponse.addProperty("status", getStatusResult().toString());

        JsonArray jResultsArray = new JsonArray();
        for (ObjectResult or : orderObjectsResult()) {
            jResultsArray.add(or.toSimplifiedJson(true));
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

    /**
     * Finalize object
     * @throws Throwable
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
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
