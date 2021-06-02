import es.um.asio.service.util.Utils;
import org.junit.Assert;
import org.junit.Test;


public class Test1 {

    private static final String BASE_URL = "http://example.org/";

    @Test
    public void test_1(){
        System.out.println(Utils.normalizeUri("SCOPUS:12345678990"));
        System.out.println(isIdFormat("localId"));
        System.out.println(isIdFormat("idLocal"));
        System.out.println(isIdFormat("esIdLocal"));
        Assert.assertTrue(true);
    }

    public boolean isIdFormat(String field) {
        for (String w : field.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            if (w.toLowerCase().equals("id"))
                return true;
        }
        return false;
    }
}
