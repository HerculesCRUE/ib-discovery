package es.um.asio.service.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties("data-behaviour") // prefix app, find app.* values
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataBehaviour {

    private List<Behaviour> behaviours = new ArrayList<>();

    public boolean ignoreAttribute(String className, String attributeName){
        List<Behaviour> behavioursFiltered = behaviours.stream().filter(b->b.getClassName().equalsIgnoreCase(className)).collect(Collectors.toList());
        if (behavioursFiltered.size()>0) {
            for (Behaviour behaviour : behavioursFiltered) {
                boolean willBeIgnored = behaviour.attributeWillBeIgnored(attributeName);
                if (willBeIgnored)
                    return true;
            }
        }
        return false;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Behaviour {
        private String className;
        private List<String> ignoreLinks = new ArrayList<>();

        public boolean attributeWillBeIgnored(String attributeName) {
            return ignoreLinks.contains(attributeName);
        }
    }
}
