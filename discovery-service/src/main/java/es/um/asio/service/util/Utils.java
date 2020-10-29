package es.um.asio.service.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import es.um.asio.service.model.AttributeType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class Utils {

    public static boolean isInteger(String s) {
        String regex = "\\d+";
        return s.trim().matches(regex);
    }

    public static boolean isNumber(String s) {
        String regex = "^[1-9]\\d*\\.(\\d+)?$";
        return s.trim().matches(regex);
    }

    public static boolean isValidNumber(String s) {
        return isInteger(s) || isNumber(s);
    }

    public static boolean isBoolean(String s) {
        String regex = "^(true|false|yes|no|si|no|s|n)$";
        return s.trim().toLowerCase().matches(regex);
    }

    public static boolean isDate(String s) {
        for (Locale locale: Locale.getAvailableLocales()) {
            if (GenericValidator.isDate(s,locale))
                return true;
        }
        try {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.UK).parse(s);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isObject(String s) {
        try {
            JsonElement je = new Gson().fromJson(s, JsonElement.class);
            if (je.isJsonArray() || je.isJsonObject())
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static AttributeType getAttributeType(String s) {
        if (isDate(s))
            return AttributeType.DATE;
        else if (isBoolean(s))
            return AttributeType.BOOLEAN;
        else if (isInteger(s))
            return AttributeType.INTEGER;
        else if (isNumber(s))
            return AttributeType.NUMBER;
        else if (isObject(s))
            return AttributeType.OBJECT;
        else
            return AttributeType.STRING;
    }

    public static boolean isValidString(String s) {
        return s != null && !s.equals("");
    }

    public static String normalize(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        s = s.replaceAll("\\p{Punct}","");
        return StringUtils.stripAccents(s);
    }

    public static boolean isPrimitive(Object o) {
        return ClassUtils.isPrimitiveOrWrapper(o.getClass()) || o instanceof String;
    }

    public static boolean containsRegex(String text, String regex) {
        return text.matches(regex);
    }

    public static boolean isValidURL(String url) {
        return new UrlValidator().isValid(url);
    }

    public static String getLastFragmentURL(String url) {
        String [] urlParts = url.split("/");
        return urlParts[urlParts.length-1];
    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =
                new Comparator<K>() {
                    public int compare(K k1, K k2) {
                        int compare = map.get(k2).compareTo(map.get(k1));
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                };
        Map<K, V> sortedByValues =
                new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }
}
