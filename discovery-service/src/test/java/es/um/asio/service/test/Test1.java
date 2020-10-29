package es.um.asio.service.test;

import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Test1 {
    @Test
    public void main(){
        HashMap<String, Float> map = new HashMap<String, Float>();

        map.put("D", 1.5f);
        map.put("B", 67.4f);
        map.put("C", 67.8f);
        map.put("A", 67.3f);

        Map<String, Float> sorted = Utils.sortByValues(map);

        System.out.println(sorted);

    }
}
