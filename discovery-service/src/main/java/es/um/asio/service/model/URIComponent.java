package es.um.asio.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class URIComponent {

    private String domain;
    private String subDomain;
    private String language;
    private String type;
    private String concept;
    private String reference;

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
}
