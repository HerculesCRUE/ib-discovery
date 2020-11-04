package es.um.asio.service.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    @Test
    void isInteger() {
        Assert.assertTrue(Utils.isInteger("1996"));
    }

    @Test
    void isNumber() {
        Assert.assertTrue(Utils.isNumber("1996.01"));
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
    }
}