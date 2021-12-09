package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.service.CacheService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.*;

/**
 * ObjectResult Class. In relational model the Object Result entity. Object Result is a result of search of similarities. Can be recursive. Object result is similar in relational model to TripleObject object
 * @see JobRegistry
 * @see Attribute
 * @see ObjectResult
 * @see ObjectResult
 * @see MergeAction
 * @see Action
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Entity
@Table(name = ObjectResult.TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ObjectResult implements Comparable<ObjectResult>,Cloneable{

    @Transient
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(ObjectResult.class);

    public static final String TABLE = "object_result";

    public static final String SIMILARITY = "similarity";
    public static final String SIMILARITY_NO_ID = "similarityWithoutId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Version
    private Long version;

    @Column(name = Columns.ORIGIN, nullable = false,columnDefinition = "VARCHAR(40)",length = 40)
    @Enumerated(value = EnumType.STRING)
    private Origin origin;

    @Column(name = Columns.NODE, nullable = true,columnDefinition = "VARCHAR(100)",length = 100)
    @EqualsAndHashCode.Include
    private String node;

    @Column(name = Columns.TRIPLE_STORE, nullable = true,columnDefinition = "VARCHAR(100)",length = 100)
    @EqualsAndHashCode.Include
    private String tripleStore;

    @Column(name = Columns.CLASS_NAME, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    @EqualsAndHashCode.Include
    private String className;

    @Column(name = Columns.LOCAL_URI, nullable = true,columnDefinition = "VARCHAR(800)",length = 800)
    @EqualsAndHashCode.Include
    private String localURI;

    @Column(name = Columns.CANONICAL_URI, nullable = true,columnDefinition = "VARCHAR(800)",length = 800)
    @EqualsAndHashCode.Include
    private String canonicalURI;

    @Column(name = Columns.LAST_MODIFICATION, nullable = false,columnDefinition = "DATETIME")
    private Date lastModification;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private JobRegistry jobRegistry;

    @Column(name = Columns.ENTITY_ID, nullable = true,columnDefinition = "VARCHAR(200)",length = 200)
    @EqualsAndHashCode.Include
    private String entityId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "objectResult", cascade = CascadeType.ALL)
    private Set<Attribute> attributes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentAutomatic", cascade = CascadeType.ALL)
    private Set<ObjectResult> automatic;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private ObjectResult parentAutomatic;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentManual", cascade = CascadeType.ALL)
    private Set<ObjectResult> manual;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private ObjectResult parentManual;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentLink", cascade = CascadeType.ALL)
    private Set<ObjectResult> link;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private ObjectResult parentLink;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "objectResultParent", cascade = CascadeType.ALL)
    private Set<ActionResult> actionResults;


    @Column(name = Columns.SIMILARITY, nullable = true)
    private Float similarity;

    @Column(name = Columns.SIMILARITY_WITH_OUT_ID, nullable = true)
    private Float similarityWithOutId;

    @Column(name = Columns.IS_MAIN, nullable = false)
    private boolean isMain = true;

    @Column(name = Columns.IS_AUTOMATIC, nullable = false)
    private boolean isAutomatic= false;

    @Column(name = Columns.IS_MANUAL, nullable = false)
    private boolean isManual= false;

    @Column(name = Columns.IS_MERGE, nullable = false)
    private boolean isMerge= false;

    @Column(name = Columns.IS_LINK, nullable = false)
    private boolean isLink= false;

    @Column(name = Columns.MERGE_ACTION, nullable = true,columnDefinition = "VARCHAR(40)",length = 40)
    @Enumerated(value = EnumType.STRING)
    private MergeAction mergeAction;

    @Column(name = Columns.STATE, nullable = true,columnDefinition = "VARCHAR(40)",length = 40)
    @Enumerated(value = EnumType.STRING)
    private State state;

    @JsonIgnore
    @ManyToOne(optional = true, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private ActionResult actionResultParent;

    /**
     * Constructor
     * @see JobRegistry
     * @see TripleObject
     * @param jobRegistry JobRegistry. Job registry parent of ObjectResult
     * @param to TripleObject. triple object related to Object Result
     * @param similarity Float. Value of the similarity
     * @param similarityWithOutId Float. Value of the similarity without id
     */
    public ObjectResult(Origin origin, State state,JobRegistry jobRegistry, TripleObject to, Float similarity, Float similarityWithOutId) {
        this.className = to.getClassName();
        this.origin = origin;
        this.state = state;
        this.node = to.getTripleStore().getNode().getNodeName();
        this.tripleStore = to.getTripleStore().getName();
        this.localURI = to.getLocalURI();
        this.canonicalURI = to.getCanonicalURI();
        this.jobRegistry = jobRegistry;
        this.lastModification = new Date(to.getLastModification());
        this.entityId = to.getId();
        this.automatic = new HashSet<>();
        this.manual = new HashSet<>();
        this.link = new HashSet<>();
        this.actionResults = new HashSet<>();
        this.attributes = new HashSet<>();
        if (similarity!=null)
            this.similarity = similarity;
        if (similarityWithOutId!=null)
            this.similarityWithOutId = similarityWithOutId;
        for (Map.Entry<String, Object> attEntry : to.getAttributes().entrySet()) {
            try {
                this.attributes.add(new Attribute(attEntry.getKey(), attEntry.getValue(), this));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public ObjectResult(JobRegistry jr, Tuple t) {
        this.jobRegistry = jr;
        this.id = ((t.get("or_id")!=null)?(Long.valueOf(t.get("or_id").toString())):null);
        this.canonicalURI = (t.get("or_canonical_uri")!=null)?((String)t.get("or_canonical_uri")):null;
        this.className = (t.get("or_class_name")!=null)?((String)t.get("or_class_name")):null;
        this.entityId = (t.get("or_entity_id")!=null)?((String)t.get("or_entity_id")):null;
        this.isAutomatic = (t.get("or_is_automatic")!=null)?((boolean)t.get("or_is_automatic")):null;
        this.isLink = (t.get("or_is_link")!=null)?((boolean)t.get("or_is_link")):null;
        this.isMain = (t.get("or_is_main")!=null)?((boolean)t.get("or_is_main")):null;
        this.isManual = (t.get("or_is_manual")!=null)?((boolean)t.get("or_is_manual")):null;
        this.isMerge = (t.get("or_is_merge")!=null)?((boolean)t.get("or_is_merge")):null;
        this.lastModification = (t.get("or_last_modification")!=null)?((Date)t.get("or_last_modification")):null;
        this.localURI = (t.get("or_local_uri")!=null)?((String)t.get("or_local_uri")):null;
        this.mergeAction = (t.get("or_merge_action")!=null)?(MergeAction.getFromString(t.get("or_merge_action").toString())):null;
        this.node = (t.get("or_node")!=null)?((String)t.get("or_node")):null;
        this.origin = (t.get("or_origin")!=null)?(Origin.getFromString(t.get("or_origin").toString())):null;
        this.similarity = ((t.get("or_similarity")!=null)?(Float.valueOf(t.get("or_similarity").toString())):null);
        this.similarityWithOutId = ((t.get("or_similarity_no_id")!=null)?(Float.valueOf(t.get("or_similarity_no_id").toString())):null);
        this.state = (t.get("or_state")!=null)?(State.getFromString(t.get("or_state").toString())):null;
        this.tripleStore = (t.get("or_triple_store")!=null)?((String)t.get("or_triple_store")):null;
        this.version = ((t.get("or_version")!=null)?(Long.valueOf(t.get("or_version").toString())):null);
        this.attributes = new HashSet<>();
        this.automatic = new HashSet<>();
        this.manual = new HashSet<>();
        this.link = new HashSet<>();
        this.actionResults = new HashSet<>();
    }

    public void copy(ObjectResult or) {
        this.id = or.getId();
        this.version = or.getVersion();
        this.origin = or.getOrigin();
        this.node = or.getNode();
        this.tripleStore = or.getTripleStore();
        this.className = or.getClassName();
        this.localURI = or.getLocalURI();
        this.canonicalURI = or.getCanonicalURI();
        this.lastModification = or.getLastModification();
        this.jobRegistry = or.getJobRegistry();
        this.entityId = or.getEntityId();
        setAttributes(or.getAttributes());
        setAutomatic(or.getAutomatic());
        this.parentAutomatic = or.getParentAutomatic();
        setManual(or.getManual());
        this.parentManual = or.getParentManual();
        setManual(or.getManual());
        this.parentManual = or.getParentManual();
        setLink(or.getLink());
        this.parentLink = or.getParentLink();
        setActionResults(or.getActionResults());
        this.similarity = or.getSimilarity();
        this.similarityWithOutId = or.getSimilarityWithOutId();
        this.isMain = or.isMain;
        this.isAutomatic = or.isAutomatic;
        this.isManual = or.isManual;
        this.isMerge = or.isMerge;
        this.isLink = or.isLink;
        this.mergeAction = or.mergeAction;
        this.state = or.state;
        this.actionResultParent = or.actionResultParent;
    }


    /**
     * Add a Automatic results to Object Result
     * @see ObjectResult
     * @param objectResult ObjectResult. Object result to add in automatic
     */
    public void addAutomatic(ObjectResult objectResult){
        objectResult.isMain = false;
        objectResult.isAutomatic = true;
        objectResult.setParentAutomatic(this);
        this.automatic.add(objectResult);
    }

    /**
     * Add a Manual results to Object Result
     * @see ObjectResult
     * @param objectResult ObjectResult. Object result to add in Manual
     */
    public void addManual(ObjectResult objectResult){
        objectResult.isMain = false;
        objectResult.isManual = true;
        objectResult.setParentManual(this);
        this.manual.add(objectResult);
    }

    /**
     * Cast JobRegistry to TripleObject
     * @see ObjectResult
     * @param objectResult ObjectResult. Object result to add in Manual
     */
    public TripleObject toTripleObject(JobRegistry jr) {
        JobRegistry jobRegistryInner = getRecursiveJobRegistry();
        if (jobRegistryInner == null)
            jobRegistryInner = jr;
        LinkedTreeMap<String,Object> attrs = getAttributesAsMap(attributes, new LinkedTreeMap<>() );
        TripleObject to = new TripleObject(getNode(), getTripleStore(),jobRegistryInner.getClassName(),attrs);
        to.setLocalURI(getLocalURI());
        to.setCanonicalURI(getCanonicalURI());
        to.setId(this.entityId);
        return to;
    }

    /**
     * Build a LinkedTreeMap<String,Object> from Set<Attribute> in recursive way
     * @param attributesSet Set<Attribute>. Set ob attributes ob ObjectResult
     * @param attrs LinkedTreeMap<String,Object> of attributes. At call method this are empty. Is used by recursion
     * @return
     */
    public LinkedTreeMap<String,Object> getAttributesAsMap(Set<Attribute> attributesSet,LinkedTreeMap<String,Object> attrs) {
        for (Attribute attribute :attributesSet) {
            String key = attribute.getKey();
            List<Value> values = new ArrayList<>(attribute.getValues());
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

    /**
     * Get Parent JobRegistry in recursive way
     * @return JobRegistry
     */
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

    /**
     * Cast to Simplified Json from instance
     * @param expands boolean. If true expand recursively ObjectResult nested
     * @return JsonObject
     */
    public JsonObject toSimplifiedJson(boolean expands, CacheService cacheService) {
        JsonObject jResponse = new JsonObject();
        jResponse.addProperty("node",getNode());
        jResponse.addProperty("tripleStore",getTripleStore());
        jResponse.addProperty("className",getClassName());
        jResponse.addProperty("entityId",getEntityId());
        jResponse.addProperty("localUri",getLocalURI());
        jResponse.addProperty("origin",getOrigin().toString());
        jResponse.addProperty("state",getState().toString());
        jResponse.addProperty("linksToEntity",0);
        if (cacheService!=null) {
            if (
                    cacheService.getInverseMap().containsKey(node) &&
                    cacheService.getInverseMap().get(node).containsKey(tripleStore) &&
                    cacheService.getInverseMap().get(node).get(tripleStore).containsKey(className) &&
                    cacheService.getInverseMap().get(node).get(tripleStore).get(className).containsKey(entityId)
            ) {
                jResponse.addProperty("linksToEntity", cacheService.getInverseMap().get(node).get(tripleStore).get(className).get(entityId).size() );
            }
        }
        jResponse.addProperty("id",getId());
        if (getSimilarity()!=null)
            jResponse.addProperty(SIMILARITY,getSimilarity());
        if (getSimilarityWithOutId()!=null)
            jResponse.addProperty(SIMILARITY_NO_ID,getSimilarityWithOutId());
        LinkedTreeMap<String,Object> attrsMap = getAttributesAsMap(attributes, new LinkedTreeMap<>());
        jResponse.add("attributes",new Gson().toJsonTree(attrsMap).getAsJsonObject());
        if (getAutomatic()!=null && expands) {
            JsonArray jAutomatics = new JsonArray();
            for (ObjectResult or : getAutomatic()) {
                jAutomatics.add(or.toSimplifiedJson(false,cacheService));
            }
            jResponse.add("automatics",jAutomatics);
        }
        if (getManual()!=null && expands) {
            JsonArray jManuals = new JsonArray();
            for (ObjectResult or : getManual()) {
                jManuals.add(or.toSimplifiedJson(false, cacheService));
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
                    jObjectResultActionsArray.add(or.toSimplifiedJson(false,cacheService));
                }
                jAction.add("items",jObjectResultActionsArray);
                jActionResults.add(jAction);
            }
            jResponse.add("actions",jActionResults);
        }
        return jResponse;
    }

    public boolean isMain() {
        return isMain;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public boolean isManual() {
        return isManual;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public boolean isLink() {
        return isLink;
    }

    public void setStateFromChild() {
        for (ObjectResult or : getAutomatic()) {
            if (or.getState() == State.OPEN) {
                this.state = State.OPEN;
                return;
            }
        }

        for (ObjectResult or : getManual()) {
            if (or.getState() == State.OPEN) {
                this.state = State.OPEN;
                return;
            }
        }

        for (ObjectResult or : getLink()) {
            if (or.getState() == State.OPEN) {
                this.state = State.OPEN;
                return;
            }
        }
        this.state = State.CLOSED;
    }

    public Float getMaxSimilarity() {
        Float maxSimilarity = Math.max(((getSimilarity()!=null)?getSimilarity():0f),((getSimilarityWithOutId()!=null)?getSimilarityWithOutId():0f));
        for (ObjectResult or : getAutomatic()) {
            Float localMax = Math.max(((or.getSimilarity()!=null)?or.getSimilarity():0f),((or.getSimilarityWithOutId()!=null)?or.getSimilarityWithOutId():0f));
            if (localMax>maxSimilarity)
                maxSimilarity = localMax;
        }
        for (ObjectResult or : getManual()) {
            Float localMax = Math.max(((or.getSimilarity()!=null)?or.getSimilarity():0f),((or.getSimilarityWithOutId()!=null)?or.getSimilarityWithOutId():0f));
            if (localMax>maxSimilarity)
                maxSimilarity = localMax;
        }
        for (ObjectResult or : getLink()) {
            Float localMax = Math.max(((or.getSimilarity()!=null)?or.getSimilarity():0f),((or.getSimilarityWithOutId()!=null)?or.getSimilarityWithOutId():0f));
            if (localMax>maxSimilarity)
                maxSimilarity = localMax;
        }
        return maxSimilarity;
    }

    @Override
    public int compareTo(ObjectResult o) {
        return this.getMaxSimilarity().compareTo(o.getMaxSimilarity());
    }


    @Override
    public ObjectResult clone() throws CloneNotSupportedException {
        return (ObjectResult) super.clone();
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
         * NODE column.
         */
        protected static final String NODE = "node";
        /**
         * TRIPLE_STORE column.
         */
        protected static final String TRIPLE_STORE = "triple_Store";
        /**
         * LOCAL_URI column.
         */
        protected static final String LOCAL_URI = "local_uri";
        /**
         * CANOCIA column.
         */
        protected static final String CANONICAL_URI = "canonical_uri";
        /**
         * LAST_MODIFICATION column.
         */
        protected static final String LAST_MODIFICATION = "last_modification";
        /**
         * ENTITY_ID column.
         */
        protected static final String ENTITY_ID = "entity_id";
        /**
         * SIMILARITY column.
         */
        protected static final String SIMILARITY = "similarity";

        /**
         * SIMILARITY_WITH_OUT_ID column.
         */
        protected static final String SIMILARITY_WITH_OUT_ID = "similarity_no_id";

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
         * IS_MERGE column.
         */
        protected static final String IS_MERGE = "is_merge";
        /**
         * IS_LINK column.
         */
        protected static final String IS_LINK = "is_link";
        /**
         * MERGE_ACTION column.
         */
        protected static final String MERGE_ACTION = "merge_action";
        /**
         * STATE column.
         */
        protected static final String STATE = "state";
        /**
         * ORIGIN column.
         */
        protected static final String ORIGIN = "origin";

    }
}
