package es.um.asio.service.model.rdf;


import com.github.jsonldjava.shaded.com.google.common.io.CharSource;
import com.github.jsonldjava.shaded.com.google.common.io.CharStreams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;

import java.io.*;

import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.mapper.ObjectMapperDeserializationContext;
import com.jayway.restassured.mapper.ObjectMapperSerializationContext;

public class RdfObjectMapper implements ObjectMapper  {

    private String baseURI;

    public RdfObjectMapper() {
        this.baseURI = "";
    }

    public RdfObjectMapper(String baseURI) {
        this.baseURI = baseURI;
    }

    private String getLang(String mediaType) {
        if (MediaTypes.TEXT_TURTLE.equals(mediaType)) {
            return "TURTLE";
        } else if (MediaTypes.APPLICATION_RDF_XML.equals(mediaType)) {
            return "RDF/XML";
        } else if (MediaTypes.APPLICATION_JSON.equals(mediaType) ||
                MediaTypes.APPLICATION_LD_JSON.equals(mediaType)) {
            return "JSON-LD";
        } else  if (MediaTypes.APPLICATION_SPARQL_UPDATE.equals(mediaType) ||
                MediaTypes.APPLICATION_SPARQL_UPDATE.equals(mediaType)) {
            return "sparql-update";
        }

        throw new IllegalArgumentException("Unsupported media type: " + mediaType);
    }

    @Override
    public Object deserialize(ObjectMapperDeserializationContext context) {
        InputStream input = context.getDataToDeserialize().asInputStream();
        String text = null;
        try (Reader reader = new InputStreamReader(input)) {
            text = CharStreams.toString(reader);
        } catch (Exception e) {
            System.out.println();
        }
        if (text!=null) {
            InputStream targetStream =
                    null;
            try {
                targetStream = new ReaderInputStream(CharSource.wrap(text).openStream());
                Model m = ModelFactory.createDefaultModel();
                m.read(targetStream,baseURI, getLang(MediaTypes.TEXT_TURTLE));
                return m;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else
            return null;
    }

    @Override
    public Object serialize(ObjectMapperSerializationContext context) {
        Model model = context.getObjectToSerializeAs(Model.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String lang = getLang(context.getContentType());
        RDFWriter rdfWriter = model.getWriter(lang);
        rdfWriter.setProperty("relativeURIs", "same-document");
        rdfWriter.setProperty("allowBadURIs", "true");
        rdfWriter.write(model, out, baseURI);

        return out.toByteArray();
    }
}
