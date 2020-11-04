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
        String texto = "j.0";
        String regex = "j\\.[0-9]+";
        System.out.println(texto.matches(regex));


    }
}
