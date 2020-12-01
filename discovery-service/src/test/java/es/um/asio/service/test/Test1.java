package es.um.asio.service.test;


import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.validator.GenericValidator;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Test1 {
    @Test
    public void main(){
        System.out.println(Utils.normalize("um??2"));

    }
}
