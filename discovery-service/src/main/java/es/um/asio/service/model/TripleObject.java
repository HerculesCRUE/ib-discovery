package es.um.asio.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.comparators.entities.EntitySimilarityObj;
import es.um.asio.service.comparators.entities.EntitySimilarity;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.util.Utils;
import jdk.jshell.execution.Util;
import lombok.*;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import javax.annotation.PostConstruct;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TripleObject {

    @Expose(serialize = false, deserialize = false)
    @Transient
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(TripleObject.class);


    @Expose(serialize = true, deserialize = true)
    @Id
    private String id;
    @Expose(serialize = true, deserialize = true)
    @Field(type = FieldType.Text)
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

    public TripleObject(TripleObjectES toES) {
        this.id = toES.getId();
        this.className = toES.getClassName();
        this.lastModification = toES.getLastModification().getTime();
        this.tripleStore = toES.getTripleStore();
        this.attributes = toES.getAttributes();
        buildFlattenAttributes();
    }


    public TripleObject(TripleStore tripleStore, JsonObject jData, String className, String id, String lastMod) {

        this.className = className;
        this.id = id;
        try {
            attributes = new Gson().fromJson(jData.toString(), LinkedTreeMap.class);
            lastModification = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.UK).parse(lastMod).getTime();
        } catch (Exception e) {
            attributes = new LinkedTreeMap<>();
            lastModification = new Date().getTime();
            logger.error("ParseDateException",e.getMessage());
        }
        this.flattenAttributes = new HashMap<>();
    }

    @JsonIgnore
    public int getYear(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(this.lastModification));
        return c.get(Calendar.YEAR);
    }

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
                Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public EntitySimilarityObj compare(CacheServiceImp cacheService, TripleObject other) {
        if (equalAttributesRatio(other)< 0.5f) {
            EntitySimilarityObj eso = new EntitySimilarityObj(other);
            eso.setSimilarity(0f);
            return eso;
        }
        EntityStats entityStats = cacheService.getStatsHandler().getAttributesMap(this.getTripleStore().getNode().getNode(), this.tripleStore.getTripleStore(), this.getClassName());
        Map<String,AttributeStats> attributesMap = new HashMap<>();

        for (Map.Entry<String, AttributeStats> entry : entityStats.getAttValues().entrySet()) {
            if (entry.getValue() instanceof AttributeStats) {
                attributesMap.put(entry.getKey(), (AttributeStats) entry.getValue());
            }
        }

        return EntitySimilarity.compare(other, attributesMap,this.getAttributes(),other.getAttributes());
    }

    public float equalAttributesRatio(TripleObject other) {
        Set<String> allAttrs = new HashSet<>();
        allAttrs.addAll(this.getAttributes().keySet());
        allAttrs.addAll(other.getAttributes().keySet());
        int equals = 0;
        for (String att : allAttrs) {
            if (this.getAttributes().containsKey(att) && other.getAttributes().containsKey(att) && this.getAttributes().get(att).toString().trim().toLowerCase().equals(other.getAttributes().get(att).toString().trim().toLowerCase())) {
                equals++;
            }
        }
        return ((float)equals)/((float) allAttrs.size());
    }

    public TripleObject merge(TripleObject other) {
        boolean isNewer = new Date(this.lastModification).after(new Date(other.lastModification));
        TripleObject to;
        Set<String> atts = getAttributes().keySet();
        atts.addAll(other.getAttributes().keySet());
        if (isNewer) {
            to = this;
            for (String att: atts) {
                if (!to.getAttributes().containsKey(att)) { // Si solo esta en el segundo
                    to.getAttributes().put(att,other.getAttributes().get(att));
                } else if (!Utils.isValidString(String.valueOf(to.getAttributes().get(att))) && Utils.isValidString(String.valueOf(other.getAttributes().get(att)))) {
                    to.getAttributes().put(att,other.getAttributes().get(att));
                }
            }
        } else {
            to = other;
            for (String att: atts) {
                if (!to.getAttributes().containsKey(att)) { // Si solo esta en el segundo
                    to.getAttributes().put(att,this.getAttributes().get(att));
                } else if (!Utils.isValidString(String.valueOf(to.getAttributes().get(att))) && Utils.isValidString(String.valueOf(this.getAttributes().get(att)))) {
                    to.getAttributes().put(att,other.getAttributes().get(att));
                }
            }
        }
        return to;
    }

    public boolean hasAttribute(String att,LinkedTreeMap map) {
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
                        LinkedTreeMap val = (LinkedTreeMap) item;
                        hasAttrs = hasAttrs || ((val != null) && hasAttribute(attAux, val));
                    }
                    return hasAttrs;
                } else {
                    LinkedTreeMap val = (LinkedTreeMap) map.get(key);
                    return (val != null) && hasAttribute(attAux, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object> getAttributeValue(String att,LinkedTreeMap map) {
        if (!Utils.isValidString(att))
            return null;
        String[] attrs = att.split("\\.");
        String key = attrs[0];
        if (map == null || map.get(key)==null)
            return null;
        else if (Utils.isPrimitive(map.get(key)))
            return Arrays.asList(map.get(key));
        else {
            String attAux = String.join(".", Arrays.asList(Arrays.copyOfRange(attrs, 1, attrs.length)));
            if (map.get(key) instanceof List) {
                List<Object> values = new ArrayList<>();
                for (Object item : (List) map.get(key)) {
                    LinkedTreeMap val = (LinkedTreeMap) item;
                    if (hasAttribute(attAux,val))
                        values.addAll(getAttributeValue(attAux, val));
                }
                return values;
            } else {
                LinkedTreeMap val = (LinkedTreeMap) map.get(key);
                return getAttributeValue(attAux, val);
            }
        }
    }

    public boolean checkIfHasAttribute(String att) {
        if (this.flattenAttributes.isEmpty())
            buildFlattenAttributes();
        return this.flattenAttributes.containsKey(att);
    }

    public void buildFlattenAttributes() {
        try {
            this.flattenAttributes = new HashMap<>();
            handleFlattenAttributes(null,getAttributes(),this.flattenAttributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleFlattenAttributes(String p, Object att, Map<String,List<Object>> flattens) {
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
                        System.out.println();
                    }
                }
            }
        } else {
            assert true;
        }

    }

    public List<Object> getValueFromFlattenAttributes(String key){
        if (this.flattenAttributes == null || this.flattenAttributes.size() == 0)
            buildFlattenAttributes();
        return this.flattenAttributes.get(key);
    }


/*    public void buildFlattenAttributes() {
        try {
            if (className.contains("CvnRootBean"))
                System.out.println();
            Map<String,List<Object>> faAux = new TreeMap<>();
            if (attributes!=null) {
                for (Map.Entry<String, Object> attEntry : attributes.entrySet()) {
                    Map<String, List<Object>> res = flattAttribute(attEntry.getKey(), attEntry.getValue());
                    faAux.putAll(res);
                }
            }

            this.flattenAttributes = faAux;

            if (className.contains("CvnRootBean"))
                System.out.println();
        } catch (Exception e) {
            System.out.println();
        }
    }

    public List<Pair<String,Object>> flattAttribute(String p, Object att) {
        p = !Utils.isValidString(p)?"":p;
        if (Utils.isPrimitive(att)) {
            return new ArrayList<>(Arrays.asList(new Pair<>(p,att))); // Retorno un par en forma de lista con el path y el valor
        } else {
            if (att instanceof List) { // Si es una lista

            } else { // Entonces es un mapa
                List<Pair<String,Object>> collect = new ArrayList<>();
                for (Map.Entry<String, Object> attAux : ((LinkedTreeMap<String,Object>) att).entrySet()) { // PAra todos los attributos
                    try {
                        List<Pair<String,Object>> r = flattAttribute(p + "." + attAux.getKey(), attAux.getValue());
                        if (r!=null) {
                            collect.addAll(r);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return collect;
            }
        }

    }*/




    /*private List<Pair<String,Object>> flattAttribute(String p, Object att) {
        p = !Utils.isValidString(p)?"":p;

        Map<String,List<Object>> res = new HashMap<>();
        if (Utils.isPrimitive(att)) { // Si es un Objeto Simple
            res.put(p,Arrays.asList(att));
            return res;
        } else {
            if (att instanceof List) { // Si es una Lista
                List<Map<String,List<Object>>> lRes= new ArrayList<>();
                for (Object a: (List)att) {
                    lRes.add(flattAttribute(p, a));
                }
                Map<String,List<Object>> resInner = new HashMap<>();
                for (Map<String,List<Object>> o : lRes) {
                    for (Map.Entry<String, List<Object>> entryInner : o.entrySet()) {
                        if (!resInner.containsKey(entryInner.getKey())) {
                            resInner.put(entryInner.getKey(), new ArrayList<>());
                        }
                        resInner.get(entryInner.getKey()).add(entryInner.getValue());
                    }
                }
                return resInner;
            } else { // Si es map
                Map<String,List<Object>> aux =new HashMap<>();
                for (Map.Entry<String, Object> attAux : ((LinkedTreeMap<String,Object>) att).entrySet()) {
                    try {
                        Map<String,List<Object>> r = flattAttribute(p + "." + attAux.getKey(), attAux.getValue());
                        if (attAux.getKey().equals("@id") && r.size()>1) {
                            System.out.println();
                        }
                        if (r!=null) {
                            aux.putAll(r);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return aux;
            }
        }
    }*/



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
}

