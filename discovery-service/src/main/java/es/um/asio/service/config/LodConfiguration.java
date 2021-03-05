package es.um.asio.service.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * LOD Configuration configuration properties.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Component
@ConfigurationProperties("lod") // prefix app, find app.* values
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LodConfiguration {

    public String host;
    public int port;
    public String endpoint;
    public List<String> lodDatasets;


    /**
     * @return String. Build the URI from host, port and endPoint
     */
    public String buildCompleteURI() {
        return String.format("%s:%d%s",host,port,endpoint);
    }

    public String getDatasetsComaSeparated() {
        if (lodDatasets == null)
            return "";
        else
            return String.join(",",lodDatasets);
    }
}
