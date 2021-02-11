package es.um.asio.service.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.io.StringWriter;

@Component
public class TriplesStorageUtils {

    /**
     * To object.
     *
     * @param strModel the str model
     * @return the model
     */
    public Model toObject(String strModel) {
        return toObject(strModel, RDFLanguages.RDFXML);
    }

    /**
     * To object.
     *
     * @param strModel the str model
     * @param language the language
     * @return the model
     */
    public Model toObject(String strModel, Lang language) {
        StringReader stringReader = new StringReader(strModel);

        Model modelFromString = ModelFactory.createDefaultModel();
        RDFDataMgr.read(modelFromString, stringReader, null, language);

        return modelFromString;
    }

    /**
     * Method to transform model to string
     *
     * @param model  the model
     * @param format the format
     * @return the string
     */
    public static String toString(Model model) {
        String result = StringUtils.EMPTY;
        if (model != null) {
            String syntax = "RDF/XML-ABBREV";
            StringWriter out = new StringWriter();
            model.write(out, syntax);
            result = out.toString();
        }
        return result;
    }

    /**
     * Method to transform id to Trellis Resource id
     *
     * @param id the id
     * @return the string
     */
    public String toResourceId(String id) {
        //According Trellis documentation, Any trailing hashURI values (#foo) are removed as are any query parameters (?bar). Spaces and slashes are converted to underscores.
        //https://www.trellisldp.org/docs/trellis/current/apidocs/org/trellisldp/http/core/Slug.html
        return id.split("#")[0].split("\\?")[0].trim().replaceAll("[\\s/]+", "_");
    }

    /**
     * Removes the last word from uri.
     *
     * @param url the url
     * @return the string
     */
    public static String removeLastWordFromUri(String url) {
        String[] entries = url.split("/");
        String lastWord = entries[entries.length - 1];
        return url.substring(0, url.length() - lastWord.length());
    }

}
