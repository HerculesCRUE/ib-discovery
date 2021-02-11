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
        // create an empty Model
        Model model = ModelFactory.createDefaultModel();
        Resource r = model.createResource(BASE_URL+"Objeto/1");
        r = r.addProperty(model.createProperty(BASE_URL+"properties/","name"), "daniel");
        r = r.addProperty(model.createProperty(BASE_URL+"properties/","surname"), "ruiz santamaría");
        OutputStream out = new OutputStream() {
            private StringBuilder string = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b );
            }
            public String toString() {
                return this.string.toString();
            }
        };
        RDFDataMgr.write(out,model, Lang.JSONLD);
        System.out.println(out.toString());
    }
}
