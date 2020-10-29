package es.um.asio.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.comparators.entities.EntitySimilarityOld;
import es.um.asio.service.model.elasticsearch.TripleObjectES;
import es.um.asio.service.model.stats.AttributeStats;
import es.um.asio.service.model.stats.EntityStats;
import es.um.asio.service.service.impl.CacheServiceImp;
import es.um.asio.service.util.Utils;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public TripleObject(TripleObjectES toES) {
        this.id = toES.getId();
        this.className = toES.getClassName();
        this.lastModification = toES.getLastModification().getTime();
        this.tripleStore = toES.getTripleStore();
        this.attributes = toES.getAttributes();
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

    public EntitySimilarityObjOld compare(CacheServiceImp cacheService, TripleObject other) {
        if (equalAttributesRatio(other)< 0.5f) {
            EntitySimilarityObjOld eso = new EntitySimilarityObjOld(other);
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

        return EntitySimilarityOld.compare(other, attributesMap,this.getAttributes(),other.getAttributes());
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
        if (!Utils.isValidString(att))
            return false;
        String[] attrs = att.split("\\.");
        String key = attrs[0];
        if (map == null || map.get(key)==null)
            return false;
        else if (Utils.isPrimitive(map.get(key)))
            return true;
        else {
            String attAux = String.join(".", Arrays.asList(Arrays.copyOfRange(attrs, 1, attrs.length)));
            LinkedTreeMap val = (LinkedTreeMap) map.get(key);
            return (val != null) && hasAttribute(attAux, val);
        }
    }

    public Object getAttributeValue(String att,LinkedTreeMap map) {
        if (!Utils.isValidString(att))
            return null;
        String[] attrs = att.split("\\.");
        String key = attrs[0];
        if (map == null || map.get(key)==null)
            return null;
        else if (Utils.isPrimitive(map.get(key)))
            return map.get(key);
        else {
            String attAux = String.join(".", Arrays.asList(Arrays.copyOfRange(attrs, 1, attrs.length)));
            LinkedTreeMap val = (LinkedTreeMap) map.get(key);
            return getAttributeValue(attAux, val);
        }
    }
}

