package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumerated Class. In relational model the Action to do.
 * @see Action
 * @see ObjectResult
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Entity
@Table(name = ActionResult.TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ActionResult {

    public static final String TABLE = "action_result";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Column(name = Columns.ACTION, nullable = false,columnDefinition = "VARCHAR(40) DEFAULT 'PENDING'",length = 40)
    @Enumerated(value = EnumType.STRING)
    @EqualsAndHashCode.Include
    private Action action;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "actionResultParent", cascade = CascadeType.ALL)
    private Set<ObjectResult> objectResults;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private ObjectResult objectResultParent;

    /**
     * Constructor
     * @see Action
     * @see ObjectResult
     * @param action Action. The action
     * @param objectResultParent. ObjectResult. The parent object result
     */
    public ActionResult(Action action, ObjectResult objectResultParent) {
        this.action = action;
        this.objectResults = new HashSet<>();
        this.objectResultParent = objectResultParent;
    }

    /**
     * Add Object result to action
     * @see ObjectResult
     * @param or ObjectResult. Object Result to add
     */
    public void addObjectResult(ObjectResult or) {
        this.objectResults.add(or);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Columns {
        /**
         * ID column.
         */
        protected static final String ID = "id";
        /**
         * REQUEST_CODE column.
         */
        protected static final String ACTION = "action";
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
    }
}
