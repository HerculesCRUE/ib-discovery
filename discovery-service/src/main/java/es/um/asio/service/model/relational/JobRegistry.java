package es.um.asio.service.model.relational;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.um.asio.service.util.Utils;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobRegistry", cascade = CascadeType.MERGE)
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

    @Column(name = Columns.BODY_REQUEST, nullable = true,columnDefinition = "TEXT")
    private String bodyRequest;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobRegistry", cascade = CascadeType.ALL)
    private Set<ObjectResult> objectResults;



    public JobRegistry(DiscoveryApplication discoveryApplication, String node, String tripleStore, String className) {
        this.discoveryApplication = discoveryApplication;
        this.node = node;
        this.tripleStore = tripleStore;
        this.className = className;
        this.statusResult = StatusResult.PENDING;
        this.requestRegistries = new HashSet<>();
        this.objectResults = new HashSet<>();
    }

    public Date getMaxRequestDate() {
        if (requestRegistries == null || requestRegistries.size() == 0)
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

    public void addRequestRegistry(RequestRegistry requestRegistry) {
        requestRegistry.setJobRegistry(this);
        this.requestRegistries.remove(requestRegistry);
        this.requestRegistries.add(requestRegistry);
    }

    public String getStarDateStr() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getStartedDate());
        } catch (Exception e) {
            return null;
        }
    }

    public String getCompletedDateStr() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getCompletedDate());
        } catch (Exception e) {
            return null;
        }
    }

    public JsonObject toSimplifiedJson() {
        JsonObject jResponse = new JsonObject();
        jResponse.addProperty("node",getNode());
        jResponse.addProperty("tripleStore", getTripleStore());
        jResponse.addProperty("className", getClassName());
        jResponse.addProperty("startDate", getStarDateStr());
        jResponse.addProperty("endDate", getCompletedDateStr());
        jResponse.addProperty("status", getStatusResult().toString());

        JsonArray jResultsArray = new JsonArray();
        for (ObjectResult or : getObjectResults()) {
            jResultsArray.add(or.toSimplifiedJson(true));
        }
        jResponse.add("results",jResultsArray);
        return jResponse;
    }

    public Set<String> getWebHooks() {
        Set<String> webHooks = new HashSet<>();
        for (RequestRegistry rr : requestRegistries) {
            if (Utils.isValidString(rr.getWebHook())){
                webHooks.add(rr.getWebHook());
            }
        }
        return webHooks;
    }

    public boolean isPropagatedInKafka() {
        for (RequestRegistry rr : requestRegistries) {
            if(rr.isPropagueInKafka())
                return true;
        }
        return false;
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
         * REQUEST_CODE column.
         */
        protected static final String REQUEST_CODE = "request_code";
        /**
         * REQUEST_TYPE column.
         */
        protected static final String REQUEST_TYPE = "request_type";
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
         * REQUEST_DATE column.
         */
        protected static final String REQUEST_DATE = "request_date";
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
    }

}
