package es.um.asio.service.model;

import es.um.asio.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isUpperCase;

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

    public URIComponent(String domain, String subDomain, String language, String type, String concept, String reference) {
        this.domain = domain;
        this.subDomain = subDomain;
        this.language = language;
        this.type = type;
        this.concept = concept;
        this.reference = reference;
    }

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
