package es.um.asio.service.test;

import es.um.asio.service.util.Utils;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test1 {
    @Test
    public void main(){
        String text    =
                "j.125:codigo";

        String regex = "j\\.[0-9]+:.*";
        String url = "http://hercules.org/um/es-ES/rec/CvnAuthorBean/code";
        System.out.println(Utils.containsRegex(text,regex));

        System.out.println(Utils.isValidURL(url));

        System.out.println(Utils.getLastFragmentURL(url));

    }
}
