import es.um.asio.service.util.Utils;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.VCARD4;
import org.junit.Test;

import java.io.*;


public class Test1 {

    private static final String BASE_URL = "http://example.org/";

    @Test
    public void test_1(){
        System.out.println(Utils.normalizeUri("SCOPUS:12345678990"));
        System.out.println(isIdFormat("localId"));
        System.out.println(isIdFormat("idLocal"));
        System.out.println(isIdFormat("esIdLocal"));
    }

    public boolean isIdFormat(String field) {
        for (String w : field.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            if (w.toLowerCase().equals("id"))
                return true;
        }
        return false;
    }
}
