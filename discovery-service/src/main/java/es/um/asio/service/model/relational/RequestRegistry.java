package es.um.asio.service.model.relational;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

/**
 * CacheRegistry Class. In relational model the Cache Registry entity for audit.
 * @see RequestType
 * @see JobRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Entity
@Table(name = RequestRegistry.TABLE /*, uniqueConstraints = {@UniqueConstraint(columnNames = {RequestRegistry.Columns.USER_ID,RequestRegistry.Columns.REQUEST_CODE,RequestRegistry.Columns.REQUEST_TYPE})}*/)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegistry {

    public static final String TABLE = "request_registry";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = ObjectResult.Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Version
    private Long version;

    @Column(name = Columns.USER_ID, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    @EqualsAndHashCode.Include
    private String userId;

    @Column(name = Columns.REQUEST_CODE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    @EqualsAndHashCode.Include
    private String requestCode;

    @Column(name = Columns.REQUEST_TYPE, nullable = false,columnDefinition = "VARCHAR(40)",length = 40)
    @EqualsAndHashCode.Include
    @Enumerated(value = EnumType.STRING)
    private RequestType requestType;

    @Column(name = Columns.REQUEST_DATE, nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",insertable = true, updatable = false)
    private Date requestDate;

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private JobRegistry jobRegistry;

    @Column(name = Columns.WEB_HOOK, nullable = true,columnDefinition = "VARCHAR(600)",length = 600)
    @EqualsAndHashCode.Include
    private String webHook;

    @Column(name = Columns.EMAIL, nullable = true,columnDefinition = "VARCHAR(600)",length = 600)
    @EqualsAndHashCode.Include
    private String email;

    @Column(name = Columns.PROPAGUE_IN_KAFKA, nullable = false)
    private boolean propagueInKafka = false;

    /**
     * Constructor
     * @param userId String. the user ID
     * @param requestCode String. the user request code
     * @param requestType String. the user request type
     * @param requestDate Date. the user request date
     */
    public RequestRegistry(String userId, String requestCode, RequestType requestType, Date requestDate, String email) {
        this.userId = userId;
        this.requestCode = requestCode;
        this.requestType = requestType;
        this.requestDate = (requestDate!=null)?requestDate:new Date();
        this.email = email;
    }

    public RequestRegistry(JobRegistry jr, Tuple t) {

        this.id = ((t.get("rr_id")!=null)?(Long.valueOf(t.get("rr_id").toString())):null);
        this.email = (t.get("rr_email")!=null)?((String)t.get("rr_email")):null;
        this.propagueInKafka = (t.get("rr_propague_in_kafka")!=null)?((boolean)t.get("rr_propague_in_kafka")):null;
        this.requestCode = (t.get("rr_request_code")!=null)?((String)t.get("rr_request_code")):null;
        this.requestDate = (t.get("rr_request_date")!=null)?((Date)t.get("rr_request_date")):null;
        this.requestType = (t.get("rr_request_type")!=null)?(RequestType.getFromString(t.get("rr_request_type").toString())):null;
        this.userId = (t.get("rr_user_id")!=null)?((String)t.get("rr_user_id")):null;
        this.version = ((t.get("rr_version")!=null)?(Long.valueOf(t.get("rr_version").toString())):null);
        this.webHook = (t.get("rr_web_hook")!=null)?((String)t.get("rr_web_hook")):null;
        this.jobRegistry = jr;
    }

    public void copy(RequestRegistry rr){
        this.version = rr.getVersion();
        this.userId = rr.getUserId();
        this.requestCode = rr.getRequestCode();
        this.requestType = rr.getRequestType();
        this.requestDate = rr.getRequestDate();
        this.jobRegistry = rr.getJobRegistry();
        this.webHook = rr.getWebHook();
        this.propagueInKafka = rr.isPropagueInKafka();
    }

    /**
     * Equals
     * @param o Object. The other RequestRegistry
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestRegistry that = (RequestRegistry) o;
        return userId.equals(that.userId) &&
                requestCode.equals(that.requestCode);
    }

    /**
     * HashCode method
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, requestCode,requestType);
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
        protected static final String USER_ID = "user_id";
        /**
         * REQUEST_CODE column.
         */
        protected static final String REQUEST_CODE = "request_code";
        /**
         * REQUEST_TYPE column.
         */
        protected static final String REQUEST_TYPE = "request_type";
        /**
         * REQUEST_DATE column.
         */
        protected static final String REQUEST_DATE = "request_date";
        /**
         * WEB_HOOK column.
         */
        protected static final String WEB_HOOK = "web_hook";
        /**
         * WEB_HOOK column.
         */
        protected static final String EMAIL = "email";
        /**
         * PROPAGUE_IN_KAFKA column.
         */
        protected static final String PROPAGUE_IN_KAFKA ="propague_in_kafka";
    }
}
