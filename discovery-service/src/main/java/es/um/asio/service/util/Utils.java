package es.um.asio.service.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.um.asio.service.model.AttributeType;
import es.um.asio.service.repository.triplestore.TrellisHandler;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
        static List<String> dateFormats = generateValidFormatDates();
    static List<Locale> locales = generateLocales();

    public static List<String> generateValidFormatDates() {
        List<String> dateFormats = new ArrayList<>();
        dateFormats.addAll(
                generateCombinations(
                "d",Arrays.asList(new Integer[]{1,2}),
                "M",Arrays.asList(new Integer[]{1,2,3,5}),
                "y",Arrays.asList(new Integer[]{2,4}),
                Arrays.asList(new String[]{".","/","-"}))
        );
        dateFormats.addAll(
                generateCombinations(
                "y",Arrays.asList(new Integer[]{2,4}),
                "M",Arrays.asList(new Integer[]{1,2,3,5}),
                "d",Arrays.asList(new Integer[]{1,2}),
                Arrays.asList(new String[]{".","/","-"}))
        );

        List<String> timeFormats = new ArrayList<>();
        timeFormats.addAll(
                generateCombinationsTime(
                        "h",Arrays.asList(new Integer[]{1,2}),
                        "m",Arrays.asList(new Integer[]{1,2,3,5}),
                        "s",Arrays.asList(new Integer[]{2,4}),
                        "S",Arrays.asList(new Integer[]{0,3}),
                        Arrays.asList(new String[]{":"}))
        );
        timeFormats.addAll(
                generateCombinationsTime(
                        "H",Arrays.asList(new Integer[]{1,2}),
                        "m",Arrays.asList(new Integer[]{1,2,3,5}),
                        "s",Arrays.asList(new Integer[]{2,4}),
                        "S",Arrays.asList(new Integer[]{0,3}),
                        Arrays.asList(new String[]{":"}))
        );

        List<String> formats = new ArrayList<>();
        for (String date: dateFormats) {
            formats.add(date);
            for (String time: timeFormats) {
                formats.add(date+" "+time);
            }
        }
        formats.add("EEEEE MMMMM yyyy HH:mm:ss.SSSZ");
        formats.add("yyyy-MM-dd'T'HH:mm:ss. SSSZ");
        return formats;
    }

    public static List<String> generateCombinations(String c1, List<Integer> rep1, String c2, List<Integer> rep2, String c3, List<Integer> rep3, List<String> separators) {
        List<String> combinations = new ArrayList<>();
        for (String s: separators) {
            for (int r1 : rep1) {
                for (int r2 : rep2) {
                    for (int r3 : rep3) {
                        combinations.add(c1.repeat(r1)+s+c2.repeat(r2)+s+c3.repeat(r3));
                    }
                }
            }
        }
        return combinations;
    }

    public static List<Locale> generateLocales() {
        List<Locale> locales = new ArrayList<>();
        locales.add(new Locale ( "es" , "ES" ));
        locales.add(Locale.US);
        locales.add(Locale.ROOT);
        return locales;
    }

    public static List<String> generateCombinationsTime(String c1, List<Integer> rep1, String c2, List<Integer> rep2, String c3, List<Integer> rep3, String c4, List<Integer> rep4, List<String> separators) {
        List<String> combinations = new ArrayList<>();
        for (String s: separators) {
            for (int r1 : rep1) {
                for (int r2 : rep2) {
                    for (int r3 : rep3) {
                        for (int r4 : rep4) {
                            if (r4>0) {
                                combinations.add(c1.repeat(r1) + s + c2.repeat(r2) + s + c3.repeat(r3) + "." + c4.repeat(r4));
                            } else {
                                combinations.add(c1.repeat(r1) + s + c2.repeat(r2) + s + c3.repeat(r3));
                            }
                        }
                    }
                }
            }
        }
        return combinations;
    }

    public static Map<Locale,List<String>> getStringFormat() {
        Map<Locale,List<String>> formats = new HashMap<>();
        formats.put(Locale.ROOT,new ArrayList<String>(Arrays.asList(new String[] {"MM/DD/YY","DD/MM/YY","YY/MM/DD","MMM D, YY","M/D/YY","D/M/YY","YY/M/D","M/bD/YY",
                "bD/M/YY","YY/M/bD","MMDDYY","DDMMYY","YYMMDD","MonDDYY","DDMonYY","YYMonDD","day/YY","YY/day","D MMM, YY","YY, MMM D","Mon-DD-YYYY","DD-Mon-YYYY",
                "YYYYY-Mon-DD","Mon DD, YYYY","DD Mon, YYYY","YYYY, Mon DD","yyyyMMddZ","yyyyMMdd","yyyy-MM-dd G","yyyy-MM-ddXXX","yyyy-MM-dd'T'HH:mm:ss.SSS'['VV']'","yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","yyyy-MM-dd'T'HH:mm:ss.SSSXXX","yyyy-MM-dd'T'HH:mm:ssXXX","yyyy-DDDXXX",
                "YYYY'W'wc","YYYY-'W'w-c","yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'","yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'"})));
        formats.put(new Locale ( "es" , "ES" ),new ArrayList<String>(Arrays.asList(new String[] {"d/MM/yy","d/MM/yy H:mm","d.M.yy H:mm"})));
        formats.put(Locale.US,new ArrayList<String>(Arrays.asList(new String[] {"M/d/yy","M/d/yy","MM/dd/yy","MM-dd-yy","M-d-yy","MMM d, yyyy","MMMM d, yyyy","EEEE, MMMM d, yyyy","MMM d yyyy",
                "MMMM d yyyy","MM-dd-yyyy","M-d-yyyy","yyyy-MM-ddXXX","dd/MM/yyyy","d/M/yyyy","MM/dd/yyyy","M/d/yyyy","yyyy/M/d","M/d/yy h:mm a",
                "MM/dd/yy h:mm a","MM-dd-yy h:mm a","M-d-yy h:mm a","MMM d, yyyy h:mm:ss a","EEEE, MMMM d, yyyy h:mm:ss a z","EEE MMM dd HH:mm:ss z yyyy",
                "EEE, d MMM yyyy HH:mm:ss Z","d MMM yyyy HH:mm:ss Z","MM-dd-yyyy h:mm:ss a","M-d-yyyy h:mm:ss a","yyyy-MM-dd h:mm:ss a","yyyy-M-d h:mm:ss a",
                "yyyy-MM-dd HH:mm:ss.S","dd/MM/yyyy h:mm:ss a","d/M/yyyy h:mm:ss a","MM/dd/yyyy h:mm:ss a","M/d/yyyy h:mm:ss a","MM/dd/yy h:mm:ss a",
                "MM/dd/yy H:mm:ss","M/d/yy H:mm:ss","dd/MM/yyyy h:mm a","d/M/yyyy h:mm a","MM/dd/yyyy h:mm a","M/d/yyyy h:mm a",
                "MM-dd-yy h:mm:ss a","M-d-yy h:mm:ss a","MM-dd-yyyy h:mm a","M-d-yyyy h:mm a","yyyy-MM-dd h:mm a","yyyy-M-d h:mm a","MMM.dd.yyyy",
                "d/MMM/yyyy H:mm:ss Z","dd/MMM/yy h:mm a"})));
        formats.put(Locale.UK,new ArrayList<String>(Arrays.asList(new String[] {"dd MMMM yyyy","EEEE, d MMMM yyyy","dd-MMM-yyyy","dd MMMM yyyy HH:mm:ss z","EEEE, d MMMM yyyy HH:mm:ss 'o''clock' z",
                "dd-MMM-yyyy HH:mm:ss","dd-MMM-yyyy HH:mm:ss","dd-MMM-yy hh.mm.ss.nnnnnnnnn a"})));
        return formats;
    }


    public static boolean isInteger(String s) {
        String regex = "-?\\d+";
        return s.trim().matches(regex);
    }

    public static boolean isNumber(String s) {
        String regex = "^-?[1-9]\\d*\\.(\\d+)?$";
        return s.trim().matches(regex);
    }

    public static boolean isValidNumber(String s) {
        return isInteger(s) || isNumber(s);
    }

    public static boolean isBoolean(String s) {
        String regex = "^(true|false|yes|no|si|s|n)$";
        return s.trim().toLowerCase().matches(regex);
    }

    public static boolean getBoolean(String s) {
        s = s.strip().toLowerCase();
        if (s.equals("true") || s.equals("yes") || s.equals("si") || s.equals("s"))
            return true;
        else if (s.equals("false") || s.equals("no")  || s.equals("n"))
            return false;
        else {
            try {
                return Boolean.getBoolean(s);
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static boolean isDate(String s) {
        String regex = "[0-9]{2,4}(/|-|\\.)[0-9]{2,4}(/|-|\\.)[0-9]{2,4}((\\s|T)[0-5][0-9]:[0-5][0-9]:[0-5][0-9](Z|\\.[0-9]||\\.[0-9]{2,3})?)?";
        return s.matches(regex);
    }

    public static Date getDate(String s) {
        Map<Locale,List<String>> formats = getStringFormat();
        for (Locale l : locales) {
            for (String f: dateFormats) {
                DateFormat sdf = new SimpleDateFormat(f,l);
                try {
                    Date d = sdf.parse(s);
                    return d;
                } catch (Exception e) {
                }
            }
        }
        return null;
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
        if (o == null)
            return true;
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

    public static boolean checkIfFloat(String s) {
        try {
            String regex = "^([+-]?\\d*\\.\\d+(e\\d+)?)$";
            double f = Float.parseFloat(s);
            double d = Double.parseDouble(s);
            return s.matches(regex) && d >= Float.MIN_VALUE && d <= Float.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean checkIfDouble(String s) {
        try {
            String regex = "^([+-]?\\d*\\.\\d+(e\\d+)?)$";
            double d = Double.parseDouble(s);
            return s.matches(regex) && d >= Double.MIN_VALUE && d <= Double.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkIfInt(String s) {
        try {
            String regex = "[+-]?[0-9]+";
            double l = Long.parseLong(s);
            return s.matches(regex) && l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkIfLong(String s) {
        try {
            String regex = "[+-]?[0-9]+";
            double l = Long.parseLong(s);
            return s.matches(regex) && l >= Long.MIN_VALUE && l <= Long.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkIfBoolean(String s) {
        return isBoolean(s);
    }

    public static boolean checkIfDaten(String s) {
        return isDate(s);
    }

    public static boolean checkIfString(Object o) {
        try {
            String.valueOf(o);
            if (o instanceof String)
                return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

        public static boolean checkIfComposeStringIsSame(String str1, String str2) {
        List<String> str1List = Arrays.asList(str1.split("(?=\\p{Upper})|-"));
        List<String> str2List = Arrays.asList(str2.split("(?=\\p{Upper})|-"));
        str1List = str1List.stream().filter(s -> Utils.isValidString(s)).filter(s->!s.matches("j\\.[0-9]+:")).map(String::toLowerCase).collect(Collectors.toList());
        str2List = str2List.stream().filter(s -> Utils.isValidString(s)).filter(s->!s.matches("j\\.[0-9]+:")).map(String::toLowerCase).collect(Collectors.toList());
        for (String token : str1List) {
            if (!str2List.contains(token))
                return false;
        }
        return true;
    }

    public static String replaceSubstringByRegex(String str,String replace, JsonObject jContext, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find())
        {
            String key = matcher.group(0);
            if (jContext.has(key)) {
                return str.replace(key+replace,jContext.get(key).getAsString());
            }
        }
        return str;
    }

}



