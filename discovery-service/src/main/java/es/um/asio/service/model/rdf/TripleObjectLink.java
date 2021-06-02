package es.um.asio.service.model.rdf;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import es.um.asio.service.model.TripleObject;
import es.um.asio.service.model.URIComponent;
import es.um.asio.service.service.impl.SchemaServiceImp;
import es.um.asio.service.util.Utils;
import lombok.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TripleObjectLink is the model of TripleObject for express a link of LOD cloud.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TripleObjectLink {

    private String id;
    private String datasetName;
    private String baseURL;
    private String remoteName;
    private String localClassName;
    private Map<String,String> mapper;
    private TripleObject origin;
    private List<LinkedTreeMap<String, Object>> links;
    private LinkedTreeMap<String, Object> attributes;
    private Map<String,String> prefixes;

    /**
     * Constructor
     * @param id String. The id
     * @param datasetName String. The data set name
     * @param baseURL String. The base URL
     * @param remoteName String. The remote name
     * @param localClassName. The local class name
     */
    public TripleObjectLink(String id, String datasetName, String baseURL, String remoteName, String localClassName) {
        this.id = id;
        this.datasetName = datasetName;
        this.baseURL = baseURL;
        this.remoteName = remoteName;
        this.localClassName = localClassName;
        this.mapper = new HashMap<>();
        this.links = new ArrayList<>();
        this.attributes = new LinkedTreeMap<>();
        this.prefixes = new HashMap<>();
    }

    /**
     * Constructor
     * @param datasetName String. The data set name
     * @param baseURL String. The base URL
     * @param remoteName String. The remote name
     * @param localClassName. The local class name
     */
    public TripleObjectLink(String datasetName, String baseURL, String remoteName, String localClassName) {
        this.datasetName = datasetName;
        this.baseURL = baseURL;
        this.remoteName = remoteName;
        this.localClassName = localClassName;
        this.mapper = new HashMap<>();
        this.links = new ArrayList<>();
        this.attributes = new LinkedTreeMap<>();
        this.prefixes = new HashMap<>();
    }

    /**
     * Get the id, with URL encoded
     * @return
     */
    public String getURLEndedID(){
        return URLEncoder.encode(Utils.normalizeUri(this.getId()), StandardCharsets.UTF_8);
    }

    /**
     * Constructor
     * @param jTol JsonObject. Build TripleObjectLink from Json
     */
    public TripleObjectLink(JsonObject jTol) {
        if (jTol.has("id") && !jTol.get("id").isJsonNull())
            this.id = jTol.get("id").getAsString();
        if (jTol.has("datasetName") && !jTol.get("datasetName").isJsonNull())
            this.datasetName = jTol.get("datasetName").getAsString();
        if (jTol.has("baseURL") && !jTol.get("baseURL").isJsonNull())
            this.datasetName = jTol.get("baseURL").getAsString();
        if (jTol.has("remoteName") && !jTol.get("remoteName").isJsonNull())
            this.remoteName = jTol.get("remoteName").getAsString();
        if (jTol.has("localClassName") && !jTol.get("localClassName").isJsonNull())
            this.localClassName = jTol.get("localClassName").getAsString();
        populateMapper(jTol);
        populateLinks(jTol);
        populateAttributes(jTol);
        populatePrefixes(jTol);
    }


    /**
     * Populate Mapper attribute
     * @param jTol JsonObject. Mapper in Json
     */
    private void populateMapper(JsonObject jTol) {
        try {
            this.mapper = new Gson().fromJson(jTol.get("mapper").getAsJsonObject(), HashMap.class);
        } catch (Exception e) {
            this.mapper = new HashMap<>();
        }
    }

    /**
     * Populate Links attribute
     * @param jTol JsonObject. Links in Json
     */
    private void populateLinks(JsonObject jTol) {
        List<LinkedTreeMap<String, Object>> links = new ArrayList<>();
        Gson gson = new Gson();
        try {
            JsonArray jLinks = jTol.get("links").getAsJsonArray();
            for (JsonElement jeLink :jLinks) {
                LinkedTreeMap<String, Object> link = gson.fromJson(jeLink,LinkedTreeMap.class);
                links.add(link);
            }
            this.links = links;
        } catch (Exception e) {

        }
    }

    /**
     * Populate attributes
     * @param jTol JsonObject. Attributes in Json
     */
    private void populateAttributes(JsonObject jTol) {
        try {
            this.attributes = new Gson().fromJson(jTol.get("attributes").getAsJsonObject(), LinkedTreeMap.class);
        } catch (Exception e) {
            this.attributes = new LinkedTreeMap<>();
        }
    }

    /**
     * Populate Prefixes attribute
     * @param jTol JsonObject. Prefixes in Json
     */
    private void populatePrefixes(JsonObject jTol) {
        try {
            this.prefixes = new Gson().fromJson(jTol.get("prefixes").getAsJsonObject(), HashMap.class);
        } catch (Exception e) {
            this.mapper = new HashMap<>();
        }
    }

    /**
     * Populate Origin attribute
     * @param jTol JsonObject. Origin in Json
     */
    private void populateOrigin(JsonObject jTol) {
        try {
            this.origin = new TripleObject(jTol.get("origin").getAsJsonObject());
        } catch (Exception e) {
            this.origin = null;
        }
    }

    /**
     * Generate RDF model from TripleObjectLink
     * @see org.apache.jena.Jena
     * @param schemaService SchemaService. The Schema Service for build URLs
     * @return Model. The RDF model
     */
    public Model generateModelFromJson(SchemaServiceImp schemaService) {

        String skos = "http://www.w3.org/2004/02/skos/core#";
        String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
        String ldp = "http://www.w3.org/ns/ldp#";
        prefixes.put("skos",skos);
        prefixes.put("rdf",rdf);
        prefixes.put("rdfs",rdfs);
        prefixes.put("ldp",ldp);
        Model m = ModelFactory.createDefaultModel();
        Resource r;
        URIComponent uriComponent = new URIComponent(schemaService.domain, schemaService.subDomain, schemaService.language, "kos", this.localClassName, this.id);
        String resourceURI;
        try {
            TripleObject to = new TripleObject(this);
            JsonObject jCanonicalURIs = schemaService.createCanonicalURIFromResource(to,"kos","en-EN", "trellis",false);
            resourceURI = jCanonicalURIs.get("canonicalLanguageURI").getAsString();
        } catch (Exception e) {
            resourceURI = uriComponent.buildURIFromComponents(schemaService.getCanonicalLocalSchema());
        }
        if (Utils.isValidString(resourceURI) && Utils.isValidURL(resourceURI)) {
            r = m.createResource(resourceURI);
        } else {
            r = m.createResource();
        }

        // Metadata

        // Links
        Resource resLinkList = m.createResource(r.getURI()+"/links");
        int nLinks = 0;
        for (LinkedTreeMap<String, Object> linkEntity: this.links) {
            Resource resLink = m.createResource(resLinkList.getURI()+"/"+(++nLinks));
            if (linkEntity.containsKey("type")) {
                resLink.addProperty(new PropertyImpl(ldp+"a"),linkEntity.get("type").toString());
            }
            if (linkEntity.containsKey("link")) {
                resLink.addProperty(new PropertyImpl(skos+"broader"),m.createResource(linkEntity.get("link").toString()));
            }
            resLinkList.addProperty(new PropertyImpl(skos+"broader"),resLink);
        }
        r.addProperty(new PropertyImpl(rdfs+"range"),resLinkList);
        // attributes
        r = expandAttributes(m, r, attributes);

        try {
            m.write(System.out,"RDF/XML");
        } catch (Exception e) {
            System.out.println();
        }

        return m;
    }

    /**
     * Expands Attributes in recursive way
     * @see org.apache.jena.Jena
     * @see Model
     * @see Resource
     * @param model Model. The JENA Model
     * @param resource Resource. The JENA Resource
     * @param attributes LinkedTreeMap<String, Object>. The attributes
     * @return Resource
     */
    private Resource expandAttributes(Model model,Resource resource, LinkedTreeMap<String, Object> attributes) {
        String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
        for (Map.Entry<String, Object> attEntry: attributes.entrySet()) {
            if (attEntry.getKey().equals("@_fa"))
                continue;

            String propertyURI = expandAttributeURIFRomPrefixes(attEntry.getKey());
            if(Utils.isPrimitive(attEntry.getValue())) { // Si no es objeto
                if (Utils.isValidURL(propertyURI)) {
                    String value = String.valueOf(attEntry.getValue());
                    resource.addProperty(new PropertyImpl(propertyURI), value);
                }
            } else { // Si es objeto
                Resource innerResource = model.createResource(resource.getURI()+"/"+attEntry.getKey());
                Object o = attributes.get(attEntry.getKey());
                if (o instanceof LinkedTreeMap) {
                    innerResource = expandAttributes(model, innerResource, (LinkedTreeMap<String, Object>) attributes.get(attEntry.getKey()));
                    resource.addProperty(new PropertyImpl(propertyURI), innerResource);
                } else if (o instanceof List) {
                    Resource resourceList = model.createResource(resource.getURI()+"/"+attEntry.getKey());
                    int counter = 0;
                    for ( Object oInner :(List) o) {
                        Resource item = model.createResource(resourceList.getURI()+"/"+(++counter));
                        if (Utils.isPrimitive(o)) { // Si es primitivo // TODO: ¿que hacer si es primitivo?

                        } else if (oInner instanceof LinkedTreeMap) {
                            item = expandAttributes(model, item, (LinkedTreeMap<String, Object>) oInner);
                            resourceList.addProperty(new PropertyImpl(propertyURI), item);
                        }
                    }
                    //innerResource.addProperty(new PropertyImpl(propertyURI),resourceList);
                    resource.addProperty(new PropertyImpl(propertyURI), resourceList);
                }
                /*
                        Resource resLinkList = m.createResource();
        for (LinkedTreeMap<String, Object> linkEntity: this.links) {
            Resource resLink = m.createResource();
            if (linkEntity.containsKey("type")) {
                resLink.addProperty(new PropertyImpl(ldp+"a"),linkEntity.get("type").toString());
            }
            if (linkEntity.containsKey("link")) {
                resLink.addProperty(new PropertyImpl(skos+"broader"),m.createResource(linkEntity.get("link").toString()));
            }
            resLinkList.addProperty(new PropertyImpl(skos+"broader"),resLink);
        }
                 */
            }
        }
        return resource;
    }

    /**
     * Expand component attributes from URI
     * @param att String. The attribute
     * @return String. The expanded attribute
     */
    private String expandAttributeURIFRomPrefixes(String att) {
        String regex = "^(.*):";
        Pattern pattern = Pattern.compile("^(.*):");
        Matcher matcher = pattern.matcher(att);
        String prefix = null;
        String attName = att.replaceFirst(regex,"");
        if (matcher.find())
        {
            prefix = matcher.group(1);
        }
        if (prefix!=null && prefixes.containsKey(prefix)) {
            return buildURI(prefixes.get(prefix), Arrays.asList(new String[] {attName}));
        }
        return buildURI(prefixes.get("default"), Arrays.asList(new String[] {attName}));
    }

    /**
     * Build URI from parts
     * @param baseURL String. The base URL
     * @param uriParts List<String>. The URL parts
     */
    private String buildURI(String baseURL,List<String> uriParts) {
        List<String> uriChunks = new ArrayList<>();
        if (Utils.isValidString(baseURL)) {
            if (baseURL.endsWith("/"))
                uriChunks.add(baseURL.substring(0, baseURL.length()-1));
            else
                uriChunks.add(baseURL);
        }

        for (String part : uriParts) {
            if (Utils.isValidString(part)) {
                if (part.startsWith("/"))
                    part = part.substring(1);
                if (part.endsWith("/"))
                    part = part.substring(0,part.length()-1);
                uriChunks.add(part);
            }
        }
        return String.join("/",uriChunks);
    }

}
