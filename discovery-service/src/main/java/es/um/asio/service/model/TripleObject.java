package es.um.asio.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.comparators.entities.EntitySimilarity;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.config.Hierarchies;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.rdf.TripleObjectLink;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.util.Utils;
import lombok.*;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TripleObject Class. Generalized model for entities.
 * @see TripleObjectLink
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TripleObject {

    @Expose(serialize = false, deserialize = false)
    @Transient
    @JsonIgnore
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private final Logger logger = LoggerFactory.getLogger(TripleObject.class);


    @Expose(serialize = true, deserialize = true)
    @Id
    private String id;
    @Field(type = FieldType.Text)
    @Expose(serialize = true, deserialize = true)
    private String localURI;
    @Field(type = FieldType.Text)
    @Expose(serialize = true, deserialize = true)
    private String canonicalURI;
    @Field(type = FieldType.Text)
    @Expose(serialize = true, deserialize = true)
    private String className;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Long)
    private long lastModification;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Object)
    private TripleStore tripleStore;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Object)
    private LinkedTreeMap<String,Object> attributes;
    @JsonIgnore
    private Map<String,List<Object>> flattenAttributes;
    @JsonIgnore
    private Set<TripleObjectLink> tripleObjectLink;

    /**
     * Constructor: cast to TripleObject TripleObjectES pass in parameter
     * @see TripleObjectES
     * @param toES TripleObjectES
     */
    public TripleObject(TripleObjectES toES) {
        this.id = toES.getEntityId();
        this.className = toES.getClassName();
        this.localURI = toES.getLocalURI();
        this.lastModification = toES.getLastModification().getTime();
        this.tripleStore = toES.getTripleStore();
        this.attributes = toES.getAttributes();
        buildFlattenAttributes();
    }

    /**
     * Constructor: cast to TripleObject JsonObject pass in parameter
     * @param jTripleObject JsonObject
     */
    public TripleObject(JsonObject jTripleObject) {
        try {
            if (jTripleObject.has("id") && !jTripleObject.get("id").isJsonNull())
                this.id = jTripleObject.get("id").getAsString();
            if (jTripleObject.has("localURI") && !jTripleObject.get("localURI").isJsonNull())
                this.localURI = jTripleObject.get("localURI").getAsString();
            if (jTripleObject.has("canonicalURI") && !jTripleObject.get("canonicalURI").isJsonNull())
                this.canonicalURI = jTripleObject.get("canonicalURI").getAsString();
            if (jTripleObject.has("className") && !jTripleObject.get("className").isJsonNull())
                this.className = jTripleObject.get("className").getAsString();
            if (jTripleObject.has("node") && jTripleObject.has("tripleStore"))
                this.tripleStore = new TripleStore(jTripleObject.get("tripleStore").getAsString(), jTripleObject.get("node").getAsString());
            if (jTripleObject.has("lastModification") && !jTripleObject.get("lastModification").isJsonNull())
                this.lastModification = jTripleObject.get("lastModification").getAsLong();
            if (jTripleObject.has("attributes") && !jTripleObject.get("attributes").isJsonNull())
                this.attributes = new Gson().fromJson(jTripleObject.get("attributes").getAsJsonObject().toString(), LinkedTreeMap.class);
            buildFlattenAttributes();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Constructor: cast to TripleObject TripleObjectLink pass in parameter
     * @see TripleObjectLink
     * @param tol TripleObjectLink
     */
    public TripleObject(TripleObjectLink tol) {
        if (this.tripleObjectLink == null) {
            this.id = tol.getId();
            this.className = tol.getLocalClassName();
            this.tripleStore = new TripleStore(tol.getDatasetName(), tol.getRemoteName()) ;
            this.lastModification = new java.util.Date().getTime();
            this.attributes = tol.getAttributes();
            this.tripleObjectLink = new HashSet<>();
        }
        this.tripleObjectLink.add(tol);
    }

    /**
     * Constructor
     * @param node String node name
     * @param tripleStore String triple store name
     * @param className String class name
     * @param jData JsonObject. Attributes in format Json
     */
    public TripleObject(String node, String tripleStore, String className, JSONObject jData ) {
        this.setTripleStore(new TripleStore(tripleStore,node));
        this.className = className;
        this.attributes = new Gson().fromJson(jData.toString(), LinkedTreeMap.class);
        this.flattenAttributes = new HashMap<>();
    }

    /**
     * Constructor
     * @param node String node name
     * @param tripleStore String triple store name
     * @param className String class name
     * @param attributes LinkedTreeMap<String,Object>. Attributes in format LinkedTreeMap<String,Object>
     */
    public TripleObject(String node, String tripleStore, String className, LinkedTreeMap<String,Object> attributes ) {
        this.setTripleStore(new TripleStore(tripleStore,node));
        this.className = className;
        this.attributes = attributes;
        this.flattenAttributes = new HashMap<>();
    }


    /**
     * Constructor
     * @param tripleStore String triple store name
     * @param jData JsonObject. Attributes in format Json
     * @param className String class name
     * @param id String. The id of the entity
     * @param localURI String. The local URI of resource in the triple store
     * @param lastMod String. Date of las modification
     */
    public TripleObject(TripleStore tripleStore, JsonObject jData, String className, String id,String localURI, String canonicalURI, String lastMod) {
        this.tripleStore = tripleStore;
        this.className = className;
        this.id = id;
        this.localURI = localURI;
        this.canonicalURI = canonicalURI;
        try {
            attributes = new Gson().fromJson(jData.toString(), LinkedTreeMap.class);
            lastModification = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.UK).parse(lastMod).getTime();
        } catch (Exception e) {
            attributes = new LinkedTreeMap<>();
            lastModification = new Date().getTime();
            logger.error("ParseDateException: {}",e.getMessage());
        }
        this.flattenAttributes = new HashMap<>();
    }

    /**
     * Get year of last modification
     * @return int
     */
    @JsonIgnore
    public int getYear(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(this.lastModification));
        return c.get(Calendar.YEAR);
    }

    /**
     * Get month of last modification
     * @return int
     */
    @JsonIgnore
    public int getMonth(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(this.lastModification));
        return c.get(Calendar.MONTH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripleObject that = (TripleObject) o;
        return
                Objects.equals(id, that.id) &&
                Objects.equals(className, that.className) &&
                Objects.equals(lastModification, that.lastModification) &&
                Objects.equals(tripleStore, that.tripleStore) &&
                equalAttributes(that.attributes);
    }

    /**
     * hashCode
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * check if other attributes is equal at attributes in this instance
     * @param other LinkedTreeMap<String,Object>. The attributes of other instance to compare
     * @return boolean
     */
    private boolean equalAttributes(LinkedTreeMap<String,Object> other) {
        try {
            Set<String> allKeys = this.attributes.keySet();
            for (String oKey : other.keySet()) {
                if (!allKeys.contains(oKey))
                    allKeys.add(oKey);
            }
            for (String key : allKeys) {
                Object thisAtt = this.attributes.containsKey(key) ? this.attributes.get(key) : null;
                Object otherAtt = other.containsKey(key) ? other.get(key) : null;
                if ((thisAtt == null && otherAtt == null))
                    return true;
                else if (thisAtt == null && otherAtt != null)
                    return false;
                else if (thisAtt != null && otherAtt == null)
                    return false;
                else if (!thisAtt.equals(otherAtt))
                    return false;

            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Compare instances of Triple Objects
     * @param cacheService CacheServiceImp. Reference at CacheServiceImp instance
     * @param other TripleObject. Other triple object instance
     * @return EntitySimilarityObj. Similarity in object EntitySimilarityObj
     */
    public EntitySimilarityObj compare(CacheServiceImp cacheService, TripleObject other) {
        if (equalAttributesRatio(other)< 0.5f) {
            EntitySimilarityObj eso = new EntitySimilarityObj(other);
            eso.setSimilarity(0f);
            return eso;
        }
        EntityStats entityStats = cacheService.getStatsHandler().getAttributesMap(this.getTripleStore().getNode().getNodeName(), this.tripleStore.getName(), this.getClassName());
        Map<String,AttributeStats> attributesMap = new HashMap<>();

        for (Map.Entry<String, AttributeStats> entry : entityStats.getAttValues().entrySet()) {
            if (entry.getValue() instanceof AttributeStats) {
                attributesMap.put(entry.getKey(), entry.getValue());
            }
        }

        return EntitySimilarity.compare(other, attributesMap,this.getAttributes(),other.getAttributes());
    }

    /**
     * Compare instances of Triple Objects in lazy mode
     * @param cacheService CacheServiceImp. Reference at CacheServiceImp instance
     * @param other TripleObject. Other triple object instance
     * @return EntitySimilarityObj. Similarity in object EntitySimilarityObj
     */
    public EntitySimilarityObj compareLazzy(CacheServiceImp cacheService, TripleObject other) {
        EntityStats entityStats = cacheService.getStatsHandler().getAttributesMap(this.getTripleStore().getNode().getNodeName(), this.tripleStore.getName(), this.getClassName());
        Map<String,AttributeStats> attributesMap = new HashMap<>();

        for (Map.Entry<String, AttributeStats> entry : entityStats.getAttValues().entrySet()) {
            if (entry.getValue() instanceof AttributeStats) {
                attributesMap.put(entry.getKey(), entry.getValue());
            }
        }

        return EntitySimilarity.compare(other, attributesMap,this.getAttributes(),other.getAttributes());
    }

    /**
     * Measures the ratio of similarity of attributes
     * @param other TripleObject other
     * @return float
     */
    public float equalAttributesRatio(TripleObject other) {
        Set<String> allAttrs = new HashSet<>();
        allAttrs.addAll(this.getAttributes().keySet());
        allAttrs.addAll(other.getAttributes().keySet());
        int equals = 0;
        for (String att : allAttrs) {
            if (this.getAttributes().containsKey(att) && other.getAttributes().containsKey(att) && this.getAttributes().get(att).toString().trim().equalsIgnoreCase(other.getAttributes().get(att).toString().trim())) {
                equals++;
            }
        }
        return ((float)equals)/((float) allAttrs.size());
    }


    /**
     * Check if attribute pass as parameter is in attribute structure LinkedTreeMap<String,Object>
     * @param att String. The attribute name
     * @param map LinkedTreeMap<String,Object>. The attribute structure
     * @return boolean
     */
    public boolean hasAttribute(String att,LinkedTreeMap<String,Object> map) {
        try {
            if (!Utils.isValidString(att))
                return false;
            String[] attrs = att.split("\\.");
            String key = attrs[0];
            if (map == null || map.get(key) == null)
                return false;
            else if (Utils.isPrimitive(map.get(key)))
                return true;
            else {
                String attAux = String.join(".", Arrays.asList(Arrays.copyOfRange(attrs, 1, attrs.length)));
                if (map.get(key) instanceof List) {
                    boolean hasAttrs = false;
                    for (Object item : (List) map.get(key)) {
                        if (Utils.isPrimitive(item)) {
                            hasAttrs = hasAttrs || true ;
                        } else {
                            LinkedTreeMap<String, Object> val = (LinkedTreeMap) item;
                            hasAttrs = hasAttrs || ((val != null) && hasAttribute(attAux, val));
                        }
                    }
                    return hasAttrs;
                } else {
                    LinkedTreeMap<String,Object> val = (LinkedTreeMap) map.get(key);
                    return (val != null) && hasAttribute(attAux, val);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Get the attribute value for a name of attribute
     * @param att String. The attribute name
     * @param map LinkedTreeMap<String,Object>. The attribute structure
     * @return List<Object> of value/s. If has only one value then the List as only one element
     */
    public List<Object> getAttributeValue(String att,LinkedTreeMap<String,Object> map) {
        if (!Utils.isValidString(att))
            return new ArrayList<>();
        String[] attrs = att.split("\\.");
        String key = attrs[0];
        if (map == null || map.get(key)==null)
            return new ArrayList<>();
        else if (Utils.isPrimitive(map.get(key)))
            return Arrays.asList(map.get(key));
        else {
            String attAux = String.join(".", Arrays.asList(Arrays.copyOfRange(attrs, 1, attrs.length)));
            if (map.get(key) instanceof List) {
                List<Object> values = new ArrayList<>();
                for (Object item : (List) map.get(key)) {
                    LinkedTreeMap<String,Object> val = (LinkedTreeMap) item;
                    if (hasAttribute(attAux,val))
                        values.addAll(getAttributeValue(attAux, val));
                }
                return values;
            } else {
                LinkedTreeMap<String,Object> val = (LinkedTreeMap) map.get(key);
                return getAttributeValue(attAux, val);
            }
        }
    }

    /**
     * Check if exist Attribute in attribute structure
     * @param att String. The attribute name
     * @return boolean
     */
    public boolean checkIfHasAttribute(String att) {
        if (this.flattenAttributes.isEmpty())
            buildFlattenAttributes();
        return this.flattenAttributes.containsKey(att);
    }

    /**
     * Build a no nested Map of attributes from nested Map of attributes.
     * The keys ob object nested are in the form obj1.obj2.obj3.....value
     */
    public void buildFlattenAttributes() {
        try {
            this.flattenAttributes = new HashMap<>();
            handleFlattenAttributes(null,getAttributes(),this.flattenAttributes);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void replaceAttValue(String key,Object oldVal, Object newVal) {
        if(attributes.containsKey(key)) {
            Object currentValue = attributes.get(key);
            if (currentValue instanceof List) {
                List<Object> auxList = new ArrayList<>();
                for (Object o : (List)currentValue) {
                    if (!o.equals(oldVal))
                        auxList.add(o);
                }
                auxList.add(newVal);
                attributes.put(key,auxList);
            } else { // Si no es lista
                attributes.put(key,newVal);
            }
        } else { // Si no contiene el valor
            attributes.put(key,newVal);
        }
    }

    private void handleFlattenAttributes(String p, Object att, Map<String,List<Object>> flattens) {
        p = !Utils.isValidString(p)?"":p;
        if (att!=null) {
            if (Utils.isPrimitive(att)) { // Si es primitivo, añado a la lista de sus paths
                if (!flattens.containsKey(p)) {
                    flattens.put(p, new ArrayList<>());
                }
                flattens.get(p).add(att);
            } else { // Si no es primitivo
                if (att instanceof List) { // Si es una lista
                    for (Object attAux : (List) att) {
                        handleFlattenAttributes(p, attAux, flattens); // LLamo recursivamente por cada elemento
                    }
                } else { // Si es un objeto
                    try {
                        for (Map.Entry<String, Object> attAux : ((Map<String, Object>) att).entrySet()) { // Para cada atributo del objeto
                            handleFlattenAttributes(Utils.isValidString(p) ? p + "." + attAux.getKey() : attAux.getKey(), attAux.getValue(), flattens); // LLamo recursivamente por cada atributo
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        } else {
            assert true;
        }

    }

    /**
     * Get a value from flatten attributes
     * @param key String. The attribute name
     * @return List<Object> with de value/s
     */
    public List<Object> getValueFromFlattenAttributes(String key){
        if (this.flattenAttributes == null || this.flattenAttributes.size() == 0)
            buildFlattenAttributes();
        return this.flattenAttributes.get(key);
    }

    public LinkedTreeMap<String,Object> getAttributesChangedByMapper(Map<String,String> mapper, LinkedTreeMap<String,Object> attributesMap) {
        LinkedTreeMap <String,Object> attributesAux = new LinkedTreeMap<>();
        attributesAux.putAll(attributesMap);
        for (Map.Entry<String, Object> attEntry : attributesMap.entrySet()) {
            if (mapper.containsKey(attEntry.getKey())) { // Si el atributo esta en el mapper
                Object value = attributesMap.get(attEntry.getKey());
                attributesAux.remove(attEntry.getKey());
                attributesAux.put(mapper.get(attEntry.getKey()), value);
            }
            if (!Utils.isPrimitive(attEntry.getValue())) { // Si el valor es un objeto
                if (attEntry.getValue() instanceof Map) {
                    LinkedTreeMap<String,Object> value = getAttributesChangedByMapper(mapper, (LinkedTreeMap<String, Object>) attEntry.getValue());
                    attributesAux.put(attEntry.getKey(),value);
                } else if (attEntry.getValue() instanceof List) {
                    for (Object obj : (List) attEntry.getValue()) {
                        if (obj instanceof Map) {
                            LinkedTreeMap<String,Object> value = getAttributesChangedByMapper(mapper, (LinkedTreeMap<String, Object>) obj);
                            obj = value;
                        }
                    }
                }
            }
        }
        return attributesAux;
    }


    /**
     * Merge the attributes of two TripleObjects
     * @see TripleObject
     * @param other TripleObject. Other TripleObject
     * @return TripleObject. The merged triple Object
     */
    public TripleObject merge(TripleObject other, Hierarchies hierarchies, CacheServiceImp cache) {
        TripleObject mergedTO = null;
        TripleObject oldTO = null;
        if (this.getClassName().equals(other.getClassName()) || (!hierarchies.isChildClass(this.getClassName()) && !hierarchies.isChildClass(other.getClassName()))) {
            Map<String, Pair<String, TripleObject>> mergeTOLinksAux,oldTOLinksAux;
            if (cache!=null) {
                mergeTOLinksAux = cache.getLinksToTripleObject(this);
                oldTOLinksAux = cache.getLinksToTripleObject(other);
            } else {
                mergeTOLinksAux = new HashMap<>();
                oldTOLinksAux = new HashMap<>();
            }
            if (mergeTOLinksAux.size() >= oldTOLinksAux.size()) {
                mergedTO = this;
                oldTO = other;
            } else if (mergeTOLinksAux.size() < oldTOLinksAux.size()) {
                oldTO = this;
                mergedTO = other;
            } else {
                if (this.getLastModification() > other.getLastModification()) { // Condiciones para determinar la entidad principal
                    mergedTO = this;
                    oldTO = other;
                } else {
                    mergedTO = other;
                    oldTO = this;
                }
            }
        } else {
            if (hierarchies.isChildClass(this.getClassName())) {
                mergedTO = this;
                oldTO = other;
            } else {
                mergedTO = other;
                oldTO = this;
            }
        }
        mergedTO.attributes = mergeAttributes(mergedTO.getAttributes(),oldTO.getAttributes());
        return mergedTO;
    }

    /**
     * Merge attributes of two Triple Objects
     * @param main LinkedTreeMap<String,Object>. Attributes of main TripleObject
     * @param other LinkedTreeMap<String,Object>. Attributes of other TripleObject
     * @return LinkedTreeMap<String,Object>. New set of attributes merged
     */
    private LinkedTreeMap<String,Object> mergeAttributes(LinkedTreeMap<String,Object> main, LinkedTreeMap<String,Object> other ) {
        List<String> allKeys = new ArrayList<>(main.keySet());
        allKeys.addAll(other.keySet());
        for (String key : allKeys) {
            if (!main.containsKey(key)) { // Si el principal no lo contiene
                main.put(key,other.get(key));
            } else { // Si el principal lo contiene
                if (main.get(key) instanceof Map) { // Si es un objeto
                    if (other.containsKey(key)) { // Si el otro no lo tiene
                        main.put(key,mergeAttributes((LinkedTreeMap) main.get(key),(LinkedTreeMap) other.get(key)));
                    }
                } else if (main.get(key) instanceof List) { // Si es una lista,
                    if (other.get(key) instanceof List) {
                        main.put(key, Stream.concat(((List) main.get(key)).stream(), ((List) other.get(key)).stream())
                                .collect(Collectors.toList()));
                    } else {
                        if (other.get(key)!=null) {
                            ((List) main.get(key)).add(other.get(key));
                        }
                    }
                } else if (Utils.isValidURL(main.get(key).toString())) {
                    if (other.get(key) instanceof List) {
                        List<Object> links = new ArrayList<>();
                        links.add(main.get(key));
                        for (Object le :(List)other.get(key)) {
                            if (Utils.isValidURL(le.toString())) {
                                links.add(le);
                            }
                        }
                        main.put(key,links);
                    }
                    if (Utils.isValidURL(other.get(key).toString())) {
                        List<Object> links = new ArrayList<>();
                        links.add(main.get(key).toString());
                        links.add(other.get(key).toString());
                        main.put(key,links);
                    }
                }
            }
        }
        return main;
    }


    /**
     * Check if all values of attributes are primitive types
     * @return boolean
     */
    public boolean checkIsSimpleObject() {
        boolean isSimple = true;
        for (Object att: attributes.values()) {
            if (!Utils.isPrimitive(att)) {
                isSimple = false;
                break;
            }
        }
        return isSimple;
    }

    /**
     * Cast TripleObject to JsonObject
     * @return JsonObject
     */
    public JsonObject toJson() {
        JsonObject jTo = new JsonObject();
        jTo.addProperty("localURI",this.localURI);
        jTo.addProperty("className",this.className);
        jTo.addProperty("lastModification",this.lastModification);
        jTo.add("tripleStore",getTripleStore().toJson());
        jTo.add("attributes", new Gson().toJsonTree(getAttributes()).getAsJsonObject());
        return jTo;
    }
}

