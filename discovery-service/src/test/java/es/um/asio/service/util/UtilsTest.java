package es.um.asio.service.util;

import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class UtilsTest {
    @Test
    void isInteger() {
        Assert.assertTrue(Utils.isInteger("-1996"));
    }

    @Test
    void isNumber() {
        Assert.assertTrue(Utils.isNumber("-1996.01"));
    }

    @Test
    void isValidNumber() {
        Assert.assertTrue(Utils.isValidNumber("1996"));
    }

    @Test
    void isBoolean() {
        Assert.assertTrue(Utils.isBoolean("true"));
    }

    @Test
    void isDate() {
        Assert.assertTrue(Utils.isDate("2020/01/01"));
    }

    @Test
    void isObject() {
        Assert.assertTrue(Utils.isObject("{\"id\":1,\"name\":\"name1\"}"));
    }

    @Test
    void getAttributeType() {
        Assert.assertTrue(true);
    }

    @Test
    void isValidString() {
        Assert.assertTrue(Utils.isValidString("string1"));
        Assert.assertFalse(Utils.isValidString(""));
        Assert.assertFalse(Utils.isValidString(null));
    }


    @Test
    void getDate() throws ParseException {
        String d = "22 March 1999 05:06:07 CET";
        String f = "dd MMMM yyyy HH:mm:ss z";
        Locale l = Locale.US;
        SimpleDateFormat sdf = new SimpleDateFormat(f,l);
        Date dd = sdf.parse(d);
        Assert.assertNotNull(Utils.getDate("31-01-12"));
        Assert.assertNotNull(Utils.getDate("Saturday November 2012 10:45:42.720+0100"));
        Assert.assertNotNull(Utils.getDate("12-01-31"));
        Assert.assertNotNull(Utils.getDate("12.01.31"));
        Assert.assertNotNull(Utils.getDate("12/01/31"));
        Assert.assertNotNull(Utils.getDate("31-01-2012"));
        Assert.assertNotNull(Utils.getDate("2012-01-31"));
        Assert.assertNotNull(Utils.getDate("2012-01-31 23:59:59"));
        Assert.assertNotNull(Utils.getDate("2012-01-31 23:59:59.999"));
        Assert.assertNotNull(Utils.getDate("2012-01-31 23:59:59.999+0100"));
        Assert.assertNotNull(Utils.getDate("Saturday November 2012 10:45:42.720+0100"));
        Assert.assertNotNull(Utils.getDate("2001-07-04T12:08:56.235-0700"));
/*        Assert.assertNotNull(Utils.getDate("Monday, March 22, 1999"));
        Assert.assertNotNull(Utils.getDate("22.3.99 5:06"));*/
    }

    @Test
    void generateValidFormatDates() {
        Utils.generateValidFormatDates();
        Assert.assertTrue(true);
    }

    @Test
    void checkIfComposeStringIsSame() {
        String str1 = "GrupoInvestigacion";
        String str2 = "Grupo-Investigacion";
        String str3 = "Grupo-investigacion";
        Assert.assertTrue(Utils.checkIfComposeStringIsSame(str1,str2));
        Assert.assertTrue(Utils.checkIfComposeStringIsSame(str1,str3));
    }

    @Test
    void extractSubstringByRegex() {
        String regex = "j\\.[0-9]+";
        String s  = "j.0:Persona/6c8349cc-7260-3e62-a3b1-396831a8398f";
        JsonObject jContext = new JsonObject();
        jContext.addProperty("j.0","http://hercules.org/um/es-ES/rec/");
        jContext.addProperty("j.1","http://hercules.org/um/es-ES/rec/NO");
        jContext.addProperty("j.2","http://hercules.org/um/es-ES/rec/TAMPOCO");
        String replaced = Utils.replaceSubstringByRegex(s,":",jContext,regex);
        Assert.assertTrue(replaced.equals("http://hercules.org/um/es-ES/rec/Persona/6c8349cc-7260-3e62-a3b1-396831a8398f"));
    }

    @Test
    void isInstanceLink() {
        Assert.assertTrue(Utils.isInstanceLink("https://ldpld1desa.um.es/um/es-ES/rec/Knowledge-area/6523285d-924a-34fd-a5dd-0de31610aaf4","https://$domain$/$sub-domain$/$language$/$type$/$concept$/$reference$","ldpld1desa.um.es"));
        Assert.assertFalse(Utils.isInstanceLink("https://google.es","https://$domain$/$sub-domain$/$language$/$type$/$concept$/$reference$","ldpld1desa.um.es"));
        Assert.assertFalse(Utils.isInstanceLink("https://ldpld1desa.um.es/um/es-ES/rec/Knowledge-area/6523285d-924a-34fd-a5dd-0de31610aaf4/no","https://$domain$/$sub-domain$/$language$/$type$/$concept$/$reference$","ldpld1desa.um.es"));
    }

    @Test
    void getInstanceLink() {
        Assert.assertNotNull(Utils.getInstanceLink("https://ldpld1desa.um.es/um/es-ES/rec/Knowledge-area/6523285d-924a-34fd-a5dd-0de31610aaf4","https://$domain$/$sub-domain$/$language$/$type$/$concept$/$reference$","ldpld1desa.um.es"));
        Assert.assertNull(Utils.getInstanceLink("https://google.es","https://$domain$/$sub-domain$/$language$/$type$/$concept$/$reference$","ldpld1desa.um.es"));
        Assert.assertNull(Utils.getInstanceLink("https://ldpld1desa.um.es/um/es-ES/rec/Knowledge-area/6523285d-924a-34fd-a5dd-0de31610aaf4/no","https://$domain$/$sub-domain$/$language$/$type$/$concept$/$reference$","ldpld1desa.um.es"));
    }

    @Test
    void testIsDate() {
        Assert.assertTrue(Utils.isDate("2003-06-12T22:00:00.000"));
        Assert.assertTrue(Utils.isDate("2003-06-12T22:00:00"));
        Assert.assertTrue(Utils.isDate("2011-08-12T20:17:46.384Z+02:00"));
        Assert.assertTrue(Utils.isDate("2003-06-12T22:00:00.000+02:00"));
    }
}