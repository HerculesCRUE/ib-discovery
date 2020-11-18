package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.um.asio.service.model.TripleObject;
import lombok.*;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = ObjectResult.TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ObjectResult {

    public static final String TABLE = "object_result";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Column(name = Columns.CLASS_NAME, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    @EqualsAndHashCode.Include
    private String className;

    @Column(name = Columns.LAST_MODIFICATION, nullable = false,columnDefinition = "DATETIME")
    private Date lastModification;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private JobRegistry jobRegistry;

    @Column(name = Columns.ENTITY_ID, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    @EqualsAndHashCode.Include
    private String entityId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentAutomatic", cascade = CascadeType.ALL)
    private Set<ObjectResult> automatic;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private ObjectResult parentAutomatic;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentManual", cascade = CascadeType.ALL)
    private Set<ObjectResult> manual;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private ObjectResult parentManual;

/*    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentMerge", cascade = CascadeType.ALL)
    private Set<ObjectResult> merges;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private ObjectResult parentMerge;*/

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "objectResultParent", cascade = CascadeType.ALL)
    private Set<ActionResult> actionResults;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "objectResult", cascade = CascadeType.ALL)
    private Set<Attribute> attributes;

    @Column(name = Columns.SIMILARITY, nullable = true)
    private Float similarity;

    @Column(name = Columns.IS_MAIN, nullable = false)
    private boolean isMain = true;

    @Column(name = Columns.IS_AUTOMATIC, nullable = false)
    private boolean isAutomatic= false;

    @Column(name = Columns.IS_MANUAL, nullable = false)
    private boolean isManual= false;

    @Column(name = Columns.IS_MERGE, nullable = false)
    private boolean isMerge= false;

    @Column(name = Columns.MERGE_ACTION, nullable = true,columnDefinition = "VARCHAR(40)",length = 40)
    @Enumerated(value = EnumType.STRING)
    private MergeAction mergeAction;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private ActionResult actionResultParent;

    public ObjectResult(JobRegistry jobRegistry, TripleObject to, Float similarity) {
        this.className = to.getClassName();
        this.jobRegistry = jobRegistry;
        this.lastModification = new Date(to.getLastModification());
        this.entityId = to.getId();
        this.automatic = new HashSet<>();
        this.manual = new HashSet<>();
        this.actionResults = new HashSet<>();
        this.attributes = new HashSet<>();
        if (similarity!=null)
            this.similarity = similarity;
        for (Map.Entry<String, Object> attEntry : to.getAttributes().entrySet()) {
            this.attributes.add(new Attribute(attEntry.getKey(),attEntry.getValue(),this));
        }
    }

    public void addAutomatic(ObjectResult objectResult){
        objectResult.isMain = false;
        objectResult.isAutomatic = true;
        objectResult.setParentAutomatic(this);
        this.automatic.add(objectResult);
    }

    public void addManual(ObjectResult objectResult){
        objectResult.isMain = false;
        objectResult.isManual = true;
        objectResult.setParentAutomatic(this);
        this.manual.add(objectResult);
    }

    public TripleObject toTripleObject() {
        //  String node, String tripleStore, String className, JSONObject jData
        try {
/*            JobRegistry jr = getRecursiveJobRegistry();
            JSONObject jData = generateJsonAttributes();
            TripleObject to = new TripleObject(jr.getNode(), jr.getTripleStore(), jr.getClassName(), jData);
            return to;*/
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JobRegistry getRecursiveJobRegistry() {
        if (jobRegistry!=null)
            return this.jobRegistry;
        else if(parentAutomatic!=null)
            return parentAutomatic.getRecursiveJobRegistry();
        else if(parentManual!=null)
            return parentManual.getRecursiveJobRegistry();
        else if (actionResultParent!=null)
            return actionResultParent.getObjectResultParent().getRecursiveJobRegistry();
        else
            return null;
    }

/*    public JSONObject generateJsonAttributes() throws JSONException {
        JSONObject jData = new JSONObject();
        for(Attribute att : this.attributes) {
            Set<Value> values = att.getValues();
            String key = att.getKey();
            if (values.size() == 1 ){ // Si no es un array
                if (Utils.isPrimitive(values.toArray()[0])) { // Si es un objeto simple
                    jData.put(key,values.toArray()[0]);
                } else {
                    jData.put(key,)
                }
            }
        }
        return jData;
    }*/



/*    @ManyToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id",nullable = true)
    private ObjectResult referenceObjectResult;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.ALL)
    //@JoinColumn(nullable = true)
    private Set<ObjectResult> automatic;*/

/*    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.ALL)
    //@JoinColumn(nullable = true)
    private Set<ObjectResult> manual;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.ALL)
    //@JoinColumn(nullable = true)
    private Set<ObjectResult> mergeRequests;*/

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
         * CLASS_NAME column.
         */
        protected static final String CLASS_NAME = "class_name";
        /**
         * REQUEST_DATE column.
         */
        protected static final String LAST_MODIFICATION = "last_modification";
        /**
         * REQUEST_DATE column.
         */
        protected static final String ENTITY_ID = "entity_id";
        /**
         * SIMILARITY column.
         */
        protected static final String SIMILARITY = "similarity";
        /**
         * IS_MAIN column.
         */
        protected static final String IS_MAIN = "is_main";
        /**
         * IS_AUTOMATIC column.
         */
        protected static final String IS_AUTOMATIC = "is_automatic";
        /**
         * IS_MANUAL column.
         */
        protected static final String IS_MANUAL = "is_manual";
        /**
         * MERGE column.
         */
        protected static final String IS_MERGE = "is_merge";
        /**
         * MERGE column.
         */
        protected static final String MERGE_ACTION = "merge_action";

    }
}
