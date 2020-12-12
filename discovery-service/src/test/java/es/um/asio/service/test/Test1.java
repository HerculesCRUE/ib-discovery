package es.um.asio.service.test;


import es.um.asio.service.util.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test1 {
    @Test
    public void main(){
        Map<String,String> m = new TreeMap();
        m.put("id","1");
        m.put("name","nombre1");
        System.out.println(m.get("id"));
        Assert.assertTrue(true);
    }
}
