package es.um.asio.service.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

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
}