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

@Service
public class TextHandlerServiceImp implements TextHandlerService {

    Set<String> stopWords;

    @PostConstruct
    public void init() {
        stopWords = new HashSet<>();
        InputStream in = getClass().getResourceAsStream("/stop-words.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        reader.lines().forEach(l -> stopWords.add(l.replaceAll(",","").toLowerCase().strip()));
    }

    @Override
    public String removeStopWords(String s) {
        s = s.replaceAll("\\p{Punct}", "");
        List<String> tokens = new ArrayList<>();
        for (String token : s.split(" ")) {
            if (!stopWords.contains(token.toLowerCase().strip()))
                tokens.add(token);
        }
        return String.join(" ",tokens);
    }
}
