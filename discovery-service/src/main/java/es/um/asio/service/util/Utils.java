package es.um.asio.service.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import es.um.asio.service.model.AttributeType;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.commons.validator.GenericValidator;

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
}
