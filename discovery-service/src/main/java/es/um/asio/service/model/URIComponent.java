package es.um.asio.service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Character.isUpperCase;

/**
 * URIComponent. Entity for URI Component in URIs factory.
 * @see Node
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class URIComponent {

    private String domain;
    private String subDomain;
    private String language;
    private String type;
    private String concept;
    private String reference;

    /**
     * Constructor
     * @param domain String. The domain
     * @param subDomain String. The subDomain
     * @param language String. The language
     * @param type String. The type
     * @param concept String. The concept
     * @param reference String. The reference
     */
    public URIComponent(String domain, String subDomain, String language, String type, String concept, String reference) {
        this.domain = domain;
        this.subDomain = subDomain;
        this.language = language;
        this.type = type;
        this.concept = concept;
        this.reference = reference;
    }


    /**
     * Constructor
     * @param schema String. The URI schema in the ASIO proyect
     * @link "https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/08-Esquema_de_URIs_H%C3%A9rcules/ASIO_Izertis_ArquitecturaDeURIs.md"
     * @param uri String. The URI to decompose in components in the class
     */
    public URIComponent(String schema, String uri) {
        String regex = "^(http[s]?://www\\.|http[s]?://|www\\.)";
        List<String> cleanSchema = Arrays.asList(schema.replaceFirst(regex,"").split("/"));
        String[] cleanURI = uri.replaceFirst(regex,"").split("/");
        int domainIndex = cleanSchema.indexOf("$domain$");
        if (domainIndex >= 0 && cleanURI.length > domainIndex) {
            this.domain = cleanURI[domainIndex];
        }
        int subDomainIndex = cleanSchema.indexOf("$sub-domain$");
        if (subDomainIndex >= 0 && cleanURI.length > subDomainIndex) {
            this.subDomain = cleanURI[subDomainIndex];
        }
        int typeIndex = cleanSchema.indexOf("$type$");
        if (typeIndex >= 0 && cleanURI.length > typeIndex) {
            this.type = cleanURI[typeIndex];
        }
        int languageIndex = cleanSchema.indexOf("$language$");
        if (languageIndex >= 0 && cleanURI.length > languageIndex) {
            this.language = cleanURI[languageIndex];
        }
        int conceptIndex = cleanSchema.indexOf("$concept$");
        if (conceptIndex >= 0 && cleanURI.length > conceptIndex) {
            this.concept = cleanURI[conceptIndex];
        }
        int referenceIndex = cleanSchema.indexOf("$reference$");
        if (referenceIndex >= 0 && cleanURI.length > referenceIndex) {
            this.reference = cleanURI[referenceIndex];
        }
    }

    /**
     * Build a new URI using URIS Schema
     * @param schema String. The URI Schema
     * @return String. The URI build with the schema
     */
    public String buildURIFromComponents(String schema) {
        String protocol = schema.substring(0,schema.indexOf("://")+3);
        List<String> parts = new ArrayList<>();
        for (String part : schema.split("/")) {
            if (part.startsWith("$") && part.endsWith("$")) {
                parts.add(getUriPart(part));
            }

        }
        return protocol+ String.join("/",parts);
    }

    // http://$domain$/$sub-domain$/$language$/$type$/$reference$/$concept$
    private String getUriPart(String pattern) {
        switch (pattern) {
            case "$domain$":
                return this.domain;
            case "$sub-domain$":
                return this.subDomain;
            case "$language$":
                return this.language;
            case "$type$":
                return this.type;
            case "$reference$":
                return this.reference;
            case "$concept$":
                return this.concept;
            default:
                return null;
        }
    }

    /**
     * Denormalize concept attribute
     * @return String. Concept attribute denormalized
     */
    public String getDenormalizedConcept() {
        StringBuilder conceptAux = new StringBuilder(concept);
        while (conceptAux.indexOf("-")>=0) {
            int index = concept.indexOf("-");
            if (index==conceptAux.length()-1 || isUpperCase(conceptAux.charAt(index+1))) { // Si no es el ultimo caracter
                conceptAux.deleteCharAt(index);
            }
        }
        return conceptAux.toString();
    }
}
