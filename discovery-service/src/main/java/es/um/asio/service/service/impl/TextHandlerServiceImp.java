package es.um.asio.service.service.impl;

import es.um.asio.service.service.TextHandlerService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TextHandlerService Implementation. For remove stop words
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class TextHandlerServiceImp implements TextHandlerService {

    Set<String> stopWords;

    /**
     * On init load the Stop words dictionaries
     */
    @PostConstruct
    public void init() {
        stopWords = new HashSet<>();
        InputStream in = getClass().getResourceAsStream("/stop-words.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        reader.lines().forEach(l -> stopWords.add(l.replaceAll(",","").toLowerCase().strip()));
    }

    /**
     * Remove the stop words of the String using dictionaries in languages
     * @param s String. The text
     * @return String. The text without stop words
     */
    @Override
    public String removeStopWords(String s) {
        s = s.replaceAll("\\p{Punct}", "");
        List<String> tokens = new ArrayList<>();
        String[] words = s.split(" ");
        for (String token : words) {
            if (words.length<2 || !stopWords.contains(token.toLowerCase().strip()))
                tokens.add(token);
        }
        if (tokens.size() > 0)
            return String.join(" ",tokens);
        else
            return s;
    }
}
