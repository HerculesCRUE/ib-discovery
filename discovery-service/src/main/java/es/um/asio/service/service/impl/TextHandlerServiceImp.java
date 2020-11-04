package es.um.asio.service.service.impl;

import es.um.asio.service.service.TextHandlerService;
import es.um.asio.service.util.Utils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.nio.charset.Charset;

@Service
public class TextHandlerServiceImp implements TextHandlerService {

    Set<String> stopWords;

    @PostConstruct
    public void init() throws IOException {
        stopWords = new HashSet<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("stopWords");
        String path = url.getPath();
        File[] dir = new File(path).listFiles();
        for (File f : dir) {
            List<String> lines = Files.readAllLines(Paths.get(f.getPath()),Charset.defaultCharset());
            for (String l : lines) {
                if (Utils.isValidString(l.trim()))
                    stopWords.add(l.toLowerCase().strip());
            }
        }
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
