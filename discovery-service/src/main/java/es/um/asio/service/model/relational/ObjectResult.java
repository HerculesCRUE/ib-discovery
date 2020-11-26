package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.model.TripleObject;
import lombok.*;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.persistence.*;
import java.util.*;


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

    @Column(name = Columns.ENTITY_ID, nullable = true,columnDefinition = "VARCHAR(200)",length = 200)
    @EqualsAndHashCode.Include
    private String entityId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "objectResult", cascade = CascadeType.ALL)
    private Set<Attribute> attributes;

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


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "objectResultParent", cascade = CascadeType.ALL)
    private Set<ActionResult> actionResults;


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

    public TripleObject toTripleObject(JobRegistry jr) {
        JobRegistry jobRegistry = getRecursiveJobRegistry();
        if (jobRegistry == null)
            jobRegistry = jr;
        LinkedTreeMap<String,Object> attrs = getAttributesAsMap(attributes, new LinkedTreeMap<String,Object>() );
        TripleObject to = new TripleObject(jobRegistry.getNode(), jobRegistry.getTripleStore(),jobRegistry.getClassName(),attrs);
        to.setId(this.entityId);
        return to;
    }

    public LinkedTreeMap<String,Object> getAttributesAsMap(Set<Attribute> attributesSet,LinkedTreeMap<String,Object> attrs) {
        for (Attribute attribute :attributesSet) {
            String key = attribute.getKey();
            List<Value> values = new ArrayList<Value>(attribute.getValues());
            if (values.size() == 1) { // Si no es una lista
                DataType type = values.get(0).getDataType();
                if (type != DataType.OBJECT) {
                    attrs.put(key,values.get(0).getValueParsedToType());
                } else { // Si es un Objeto
                    attrs.put(key,getAttributesAsMap(values.get(0).getAttributes(),new LinkedTreeMap<>()));
                }
            } else if (values.size()>1) { // Si es una lista
                List<Object> vls = new ArrayList<>();
                for (Value value: values) { // Recorro todos los valores
                    DataType type = value.getDataType();
                    if (type != DataType.OBJECT) {
                        vls.add(value.getValueParsedToType());
                    } else { // Si es un Objeto
                        vls.add(getAttributesAsMap(value.getAttributes(),new LinkedTreeMap<>()));
                    }
                }
                attrs.put(key,vls);
            }
        }
        return attrs;
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

    public JsonObject toSimplifiedJson(boolean expands) {
        JsonObject jResponse = new JsonObject();
        jResponse.addProperty("entityId",getEntityId());
        LinkedTreeMap<String,Object> attrsMap = getAttributesAsMap(attributes, new LinkedTreeMap<String,Object>());
        jResponse.add("attributes",new Gson().toJsonTree(attrsMap).getAsJsonObject());
        if (getAutomatic()!=null && expands) {
            JsonArray jAutomatics = new JsonArray();
            for (ObjectResult or : getAutomatic()) {
                jAutomatics.add(or.toSimplifiedJson(false));
            }
            jResponse.add("automatics",jAutomatics);
        }
        if (getManual()!=null && expands) {
            JsonArray jManuals = new JsonArray();
            for (ObjectResult or : getManual()) {
                jManuals.add(or.toSimplifiedJson(false));
            }
            jResponse.add("manuals",jManuals);
        }
        if (getActionResults()!=null && expands) {
            JsonArray jActionResults = new JsonArray();
            for (ActionResult ar : getActionResults()) {
                JsonObject jAction = new JsonObject();
                jAction.addProperty("action",ar.getAction().toString());
                JsonArray jObjectResultActionsArray = new JsonArray();
                for (ObjectResult or : ar.getObjectResults()) {
                    jObjectResultActionsArray.add(or.toSimplifiedJson(false));
                }
                jAction.add("items",jObjectResultActionsArray);
                jActionResults.add(jAction);
            }
            jResponse.add("actions",jActionResults);
        }
        return jResponse;
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
