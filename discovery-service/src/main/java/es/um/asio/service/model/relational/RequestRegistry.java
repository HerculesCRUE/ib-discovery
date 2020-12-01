package es.um.asio.service.model.relational;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = RequestRegistry.TABLE, uniqueConstraints = {@UniqueConstraint(columnNames = {RequestRegistry.Columns.USER_ID,RequestRegistry.Columns.REQUEST_CODE})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegistry {

    public static final String TABLE = "request_registry";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ObjectResult.Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Column(name = Columns.USER_ID, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    @EqualsAndHashCode.Include
    private String userId;

    @Column(name = Columns.REQUEST_CODE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    @EqualsAndHashCode.Include
    private String requestCode;

    @Column(name = Columns.REQUEST_TYPE, nullable = false,columnDefinition = "VARCHAR(40)",length = 40)
    @Enumerated(value = EnumType.STRING)
    private RequestType requestType;

    @Column(name = Columns.REQUEST_DATE, nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",insertable = true, updatable = false)
    private Date requestDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private JobRegistry jobRegistry;

    @Column(name = Columns.WEB_HOOK, nullable = true,columnDefinition = "VARCHAR(600)",length = 600)
    @EqualsAndHashCode.Include
    private String webHook;

    @Column(name = Columns.PROPAGUE_IN_KAFKA, nullable = false)
    private boolean propagueInKafka = false;

    public RequestRegistry(String userId, String requestCode, RequestType requestType, Date requestDate) {
        this.userId = userId;
        this.requestCode = requestCode;
        this.requestType = requestType;
        this.requestDate = (requestDate!=null)?requestDate:new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestRegistry that = (RequestRegistry) o;
        return userId.equals(that.userId) &&
                requestCode.equals(that.requestCode);
    }

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
         * PROPAGUE_IN_KAFKA column.
         */
        protected static final String PROPAGUE_IN_KAFKA ="propague_in_kafka";
    }
}
