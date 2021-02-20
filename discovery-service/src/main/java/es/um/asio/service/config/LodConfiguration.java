package es.um.asio.service.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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
