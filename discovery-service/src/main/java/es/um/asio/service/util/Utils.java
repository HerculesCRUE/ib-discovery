package es.um.asio.service.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.um.asio.service.model.AttributeType;
import es.um.asio.service.model.URIComponent;
import org.apache.catalina.util.URLEncoder;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utils. General utils in the application
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class Utils {

    private Utils(){}

    static List<String> dateFormats = generateValidFormatDates();
    static List<Locale> locales = generateLocales();

    /**
     * Generate Format dates
     * @return The format dates
     */
    public static List<String> generateValidFormatDates() {
        List<String> dateFormats = new ArrayList<>();
        dateFormats.addAll(
                generateCombinations(
                        "y",buildListInt(2,4),
                        "M",buildListInt(1,2,3,5),
                        "d",buildListInt(1,2),
                        buildListStr(".","/","-"))
        );
        dateFormats.addAll(
                generateCombinations(
                "d",buildListInt(1,2),
                "M",buildListInt(1,2,3,5),
                "y",buildListInt(2,4),
                        buildListStr(".","/","-"))
        );

        List<String> timeFormats = new ArrayList<>();
        timeFormats.addAll(
                generateCombinationsTime(
                "h",buildListInt(1,2),
                "m",buildListInt(1,2,3,5),
                "s",buildListInt(2,4),
                "S",buildListInt(0,3),
                buildListStr(":"))
        );

        timeFormats.addAll(
                generateCombinationsTime(
                "H",buildListInt(1,2),
                "m",buildListInt(1,2,3,5),
                "s",buildListInt(2,4),
                "S",buildListInt(0,3),
                buildListStr(":"))
        );

        List<String> formats = new ArrayList<>();
        formats.add("yyyy-MM-dd");
        formats.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formats.add("yyyy/MM/dd");
        formats.add("yyyy/MM/dd'T'HH:mm:ss.SSS'Z'");
        formats.add("EEEEE MMMMM yyyy HH:mm:ss.SSSZ");
        for (String date: dateFormats) {
            formats.add(date);
            for (String time: timeFormats) {
                formats.add(date+" "+time);
            }
        }
        return formats;
    }

    /**
     * @param args List of integer with the length of the patterns
     * @return List<Integer>
     */
    private static List<Integer> buildListInt(int ... args) {
        List<Integer> ints = new ArrayList<>();
        for (int arg : args) {
            ints.add(arg);
        }
        return ints;
    }

    /**
     * Filter a list of classes with the pattern of the class name
     * @param classes List<String> list of classes
     * @param className String. Class from where the pattern will be inferred
     * @return boolean. True if class name pattern is in List
     */
    public static boolean machClassName(List<String> classes, String className) {
        String [] chunkedClassName = className.split("\\-");
        for (String c: classes) { // Para cada clase
            if (chunkedClassName.length == 0)
                return false;
            boolean match = true;
            for (String ccn: chunkedClassName) { // Para todos los pedazos del nombre de la clase
                match = match && c.toLowerCase().contains(ccn.toLowerCase());
            }
            if (match)
                return true;
        }
        return false;
    }

    private static List<String> buildListStr(String ... args) {
        return Arrays.asList(args);
    }

    /**
     * Generate combinations of Date Formats
     * @param c1 String. The first pattern
     * @param rep1 List<Integer>. The number of repetitions
     * @param c2 String. The second pattern
     * @param rep2 List<Integer>. The number of repetitions
     * @param c3 String. The third pattern
     * @param rep3 List<Integer>. The number of repetitions
     * @param separators List<String>. Valid separators between date parts
     * @return List<String>. List of Valid date format patterns
     */
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

    /**
     * Generate valid locales for dates
     * @return List<Locale>
     */
    public static List<Locale> generateLocales() {
        List<Locale> locales = new ArrayList<>();
        locales.add(new Locale ( "es" , "ES" ));
        locales.add(Locale.US);
        locales.add(Locale.ROOT);
        return locales;
    }

    /**
     * Generate combinations of Time Formats part
     * @param c1 String. The first pattern
     * @param rep1 List<Integer>. The number of repetitions
     * @param c2 String. The second pattern
     * @param rep2 List<Integer>. The number of repetitions
     * @param c3 String. The third pattern
     * @param rep3 List<Integer>. The number of repetitions
     * @param c4 String. The fourth pattern
     * @param rep4 List<Integer>. The number of repetitions
     * @param separators List<String>. Valid separators between date parts
     * @return List<String>. List of Valid time format patterns
     */
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

    /**
     * Generate all valid time patters
     * @return Map<Locale,List<String>>. The key is the locale and the value the valid Date Format
     */
    public static Map<Locale,List<String>> getStringFormat() {
        Map<Locale,List<String>> formats = new HashMap<>();
        formats.put(Locale.ROOT,new ArrayList<>(buildListStr("MM/DD/YY","DD/MM/YY","YY/MM/DD","MMM D, YY","M/D/YY","D/M/YY","YY/M/D","M/bD/YY",
                "bD/M/YY","YY/M/bD","MMDDYY","DDMMYY","YYMMDD","MonDDYY","DDMonYY","YYMonDD","day/YY","YY/day","D MMM, YY","YY, MMM D","Mon-DD-YYYY","DD-Mon-YYYY",
                "YYYYY-Mon-DD","Mon DD, YYYY","DD Mon, YYYY","YYYY, Mon DD","yyyyMMddZ","yyyyMMdd","yyyy-MM-dd G","yyyy-MM-ddXXX","yyyy-MM-dd'T'HH:mm:ss.SSS'['VV']'","yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","yyyy-MM-dd'T'HH:mm:ss.SSSXXX","yyyy-MM-dd'T'HH:mm:ssXXX","yyyy-DDDXXX",
                "YYYY'W'wc","YYYY-'W'w-c","yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'","yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")));
        formats.put(new Locale ( "es" , "ES" ),new ArrayList<>(Arrays.asList(new String[] {"d/MM/yy","d/MM/yy H:mm","d.M.yy H:mm"})));
        formats.put(Locale.US,new ArrayList<>(buildListStr("M/d/yy","M/d/yy","MM/dd/yy","MM-dd-yy","M-d-yy","MMM d, yyyy","MMMM d, yyyy","EEEE, MMMM d, yyyy","MMM d yyyy",
                "MMMM d yyyy","MM-dd-yyyy","M-d-yyyy","yyyy-MM-ddXXX","dd/MM/yyyy","d/M/yyyy","MM/dd/yyyy","M/d/yyyy","yyyy/M/d","M/d/yy h:mm a",
                "MM/dd/yy h:mm a","MM-dd-yy h:mm a","M-d-yy h:mm a","MMM d, yyyy h:mm:ss a","EEEE, MMMM d, yyyy h:mm:ss a z","EEE MMM dd HH:mm:ss z yyyy",
                "EEE, d MMM yyyy HH:mm:ss Z","d MMM yyyy HH:mm:ss Z","MM-dd-yyyy h:mm:ss a","M-d-yyyy h:mm:ss a","yyyy-MM-dd h:mm:ss a","yyyy-M-d h:mm:ss a",
                "yyyy-MM-dd HH:mm:ss.S","dd/MM/yyyy h:mm:ss a","d/M/yyyy h:mm:ss a","MM/dd/yyyy h:mm:ss a","M/d/yyyy h:mm:ss a","MM/dd/yy h:mm:ss a",
                "MM/dd/yy H:mm:ss","M/d/yy H:mm:ss","dd/MM/yyyy h:mm a","d/M/yyyy h:mm a","MM/dd/yyyy h:mm a","M/d/yyyy h:mm a",
                "MM-dd-yy h:mm:ss a","M-d-yy h:mm:ss a","MM-dd-yyyy h:mm a","M-d-yyyy h:mm a","yyyy-MM-dd h:mm a","yyyy-M-d h:mm a","MMM.dd.yyyy",
                "d/MMM/yyyy H:mm:ss Z","dd/MMM/yy h:mm a")));
        formats.put(Locale.UK,new ArrayList<>(buildListStr("dd MMMM yyyy","EEEE, d MMMM yyyy","dd-MMM-yyyy","dd MMMM yyyy HH:mm:ss z","EEEE, d MMMM yyyy HH:mm:ss 'o''clock' z",
                "dd-MMM-yyyy HH:mm:ss","dd-MMM-yyyy HH:mm:ss","dd-MMM-yy hh.mm.ss.nnnnnnnnn a")));
        return formats;
    }


    /**
     * Check if is integer
     * @param s String. The value to check
     * @return boolean. True if is integer
     */
    public static boolean isInteger(String s) {
        String regex = "-?\\d+";
        return s.trim().matches(regex);
    }

    /**
     * Check if is number
     * @param s String. The value to check
     * @return boolean. True if is number
     */
    public static boolean isNumber(String s) {
        String regex = "^-?[1-9]\\d*\\.(\\d+)?$";
        return s.trim().matches(regex);
    }

    /**
     * Check if is valid number
     * @param s String. The value to check
     * @return boolean. True if is a valid number
     */
    public static boolean isValidNumber(String s) {
        return isInteger(s) || isNumber(s);
    }

    /**
     * Check if is valid boolean
     * @param s String. The value to check
     * @return boolean. True if is a valid boolean
     */
    public static boolean isBoolean(String s) {
        String regex = "^(true|false|yes|no|si|s|n)$";
        return s.trim().toLowerCase().matches(regex);
    }

    /**
     * Get boolean from String if is valid boolean
     * @param s String. The value to check
     * @return boolean. The boolean value
     */
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

    /**
     * Check if is valid Date
     * @param s String. The value to check
     * @return boolean. True if is a valid Date
     */
    public static boolean isDate(String s) {
        String regex = "[0-9]{2,4}(/|-|\\.)[0-9]{2,4}(/|-|\\.)[0-9]{2,4}((\\s|T)[0-5][0-9]:[0-5][0-9]:[0-5][0-9](\\.[0-9]+||\\.[0-9]{2,3})?((Z)?\\+[0-9]{2}(:[0-9]{2})?)?)?";
        return s.matches(regex);
    }

    /**
     * Get Date from String if is valid Date
     * @param s String. The value to check
     * @return boolean. The boolean Date
     */
    public static Date getDate(String s) {

        for (Locale l : locales) {
            for (String f: dateFormats) {
                DateFormat sdf = new SimpleDateFormat(f,l);
                try {
                    Date d = sdf.parse(s);
                    return d;
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * Check if is valid Object
     * @param s String. The value to check
     * @return boolean. True if is a valid Object
     */
    public static boolean isObject(String s) {
        try {
            JsonElement je = new Gson().fromJson(s, JsonElement.class);
            boolean isObject = false;
            if (je.isJsonArray() || je.isJsonObject())
                isObject = true;
            return isObject;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if is valid Object
     * @param s String. The value to check
     * @return boolean. True if is a valid Object
     */
    // defaultSchema: https://$domain$/$sub-domain$/$language$/$type$/$concept$/$reference$
    public static boolean isInstanceLink(String s,String schema, String domain) {
        String regex = schema.replaceAll("\\$[a-zA-Z]+(?:-[a-zA-Z]+)*\\$",".*");
        return isValidURL(s) && s.contains(domain) && s.split("/").length == 8 && s.matches("^"+regex+"$");
    }

    public static URIComponent getInstanceLink(String s, String schema, String domain) {
        if (isInstanceLink(s,schema,domain)) {
            try {
                return new URIComponent(schema,s);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Get the AttributeType from String
     * @see AttributeType
     * @param s String. The value to check
     * @return AttributeType. The Attribute type
     */
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

    /**
     * Check if is valid String
     * @param s String. The value to check
     * @return boolean. True if is a valid String
     */
    public static boolean isValidString(String s) {
        return s != null && !s.equals("") && !s.toLowerCase().trim().equals("null");
    }

    /**
     * Normalize a String
     * @param s String. The String to normalize
     * @return String. The normalized String
     */
    public static String normalize(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        s = s.replaceAll("\\p{Punct}","");
        return StringUtils.stripAccents(s);
    }

    /**
     * Check if is valid java primitive
     * @param s String. The value to check
     * @return boolean. True if is a valid primitive
     */
    public static boolean isPrimitive(Object o) {
        if (o == null)
            return true;
        return ClassUtils.isPrimitiveOrWrapper(o.getClass()) || o instanceof String ;
    }

    /**
     * Check if String match with the regex
     * @param text String. The String to evaluate
     * @param regex. The regex
     * @return boolean. True if he regex match with the String
     */
    public static boolean containsRegex(String text, String regex) {
        return text.matches(regex);
    }

    /**
     * Check if the String pass in the parameter is a valid URL
     * @param url String to check
     * @return boolean. True if is valid else false
     */
    public static boolean isValidURL(String url) {
        if (url.endsWith("$"))
            return false;
        return new UrlValidator().isValid(url);
    }

    /**
     * Extract the last fragment in a URL
     * @param url String. The URL
     * @return String. The last fragment
     */
    public static String getLastFragmentURL(String url) {
        String [] urlParts = url.split("/");
        return urlParts[urlParts.length-1];
    }

    /**
     * Sort a Map for values
     * @param map Map<K, V>. The map to sort
     * @param <K>. The key in the map
     * @param <V>. The value in the map
     * @return <K, V extends Comparable<V>>. The map sorted
     */
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
                new TreeMap<>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    /**
     * Check if is valid Float
     * @param s String. The value to check
     * @return boolean. True if is a valid Float
     */
    public static boolean checkIfFloat(String s) {
        try {
            String regex = "^([+-]?\\d*\\.\\d+(e\\d+)?)$";
            double d = Double.parseDouble(s);
            return s.matches(regex) && d >= Float.MIN_VALUE && d <= Float.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if is valid Double
     * @param s String. The value to check
     * @return boolean. True if is a valid Double
     */
    public static boolean checkIfDouble(String s) {
        try {
            String regex = "^([+-]?\\d*\\.\\d+(e\\d+)?)$";
            double d = Double.parseDouble(s);
            return s.matches(regex) && d >= Double.MIN_VALUE && d <= Double.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if is valid Int
     * @param s String. The value to check
     * @return boolean. True if is a valid Int
     */
    public static boolean checkIfInt(String s) {
        try {
            String regex = "[+-]?[0-9]+";
            double l = Long.parseLong(s);
            return s.matches(regex) && l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if is valid Long
     * @param s String. The value to check
     * @return boolean. True if is a valid Long
     */
    public static boolean checkIfLong(String s) {
        try {
            String regex = "[+-]?[0-9]+";
            double l = Long.parseLong(s);
            return s.matches(regex) && l >= Long.MIN_VALUE && l <= Long.MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if is valid Boolean
     * @param s String. The value to check
     * @return boolean. True if is a valid Boolean
     */
    public static boolean checkIfBoolean(String s) {
        return isBoolean(s);
    }

    /**
     * Check if is valid Date
     * @param s String. The value to check
     * @return boolean. True if is a valid Date
     */
    public static boolean checkIfDaten(String s) {
        return isDate(s);
    }

    /**
     * Check if is valid String
     * @param s String. The value to check
     * @return boolean. True if is a valid String
     */
    public static boolean checkIfString(Object o) {
        try {
            boolean isString = false;
            String.valueOf(o);
            if (o instanceof String)
                isString = true;
            return isString;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if two composed String is same
     * @param str1 String. The first String
     * @param str2 String. The second String
     * @return boolean
     */
    public static boolean checkIfComposeStringIsSame(String str1, String str2) {
        List<String> str1List = Arrays.asList(str1.split("(?=\\p{Upper})|-"));
        List<String> str2List = Arrays.asList(str2.split("(?=\\p{Upper})|-"));
        str1List = str1List.stream().filter(Utils::isValidString).filter(s->!s.matches("j\\.[0-9]+:")).map(String::toLowerCase).collect(Collectors.toList());
        str2List = str2List.stream().filter(Utils::isValidString).filter(s->!s.matches("j\\.[0-9]+:")).map(String::toLowerCase).collect(Collectors.toList());
        for (String token : str1List) {
            if (!str2List.contains(token))
                return false;
        }
        return true;
    }

    /**
     * Replace Sub-strings in JsonObject using regex
     * @param str String. The String to search
     * @param replace String. The String to replace
     * @param jContext String. The object witch contains the attribute to replace
     * @param regex String. The regex
     * @return String. The String replaced
     */
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

    /**
     * Do generic request REST HTTP
     * @see URL
     * @see Connection.Method
     * @param url URL. The URL to request
     * @param method Connection.Method. The request method HTTP
     * @param headers Map<String,String>. The headers. Key is the header key, value is the header value
     * @param params Map<String,String>. The Params. Key is the param key, value is the param value
     * @param queryParams Map<String,String>. The Query Params. Key is the query param key, value is the query param value
     * @param encode boolean. If true then URL encode will be done
     * @return JsonElement with the HTTP response
     * @throws IOException
     */
    public static JsonElement doRequest(URL url, Connection.Method method, Map<String,String> headers, Map<String,String> params, Map<String,String> queryParams, boolean encode) throws IOException {
        if (queryParams!=null) {
            url = buildQueryParams(url,queryParams, encode);
        }
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method.toString());
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (headers!=null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                con.setRequestProperty(headerEntry.getKey(),headerEntry.getValue());
            }
        }
        if (params!=null) {
            for (Map.Entry<String, String> paramEntry : params.entrySet()) {
                con.setRequestProperty(paramEntry.getKey(),paramEntry.getValue());
            }
        }
        con.setDoOutput(true);
        StringBuilder response;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        con.disconnect();
        JsonElement jResponse = new Gson().fromJson(response.toString(), JsonElement.class);
        return jResponse;
    }

    private static URL buildQueryParams(URL baseURL, Map<String,String> queryParams,boolean encode) throws MalformedURLException, UnsupportedEncodingException {
        StringBuffer base = new StringBuffer();
        base.append(baseURL.toString());
        if (queryParams!=null && queryParams.size()>0) {
            base.append("?");
            List<String> qpList = new ArrayList<>();
            for (Map.Entry<String, String> qpEntry : queryParams.entrySet()) {
                if (encode)
                    qpList.add(qpEntry.getKey()+"="+ new URLEncoder().encode(qpEntry.getValue(), Charset.defaultCharset()));
                else
                    qpList.add(qpEntry.getKey()+"="+qpEntry.getValue());
            }
            base.append(String.join("&",qpList));
        }
        return new URL(base.toString());
    }

    /**
     * Check if name of field has Id format
     * @param field String. The name of the field
     * @return boolean
     */
    public static boolean isIdFormat(String field) {
        for (String w : field.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            if (w.toLowerCase().equals("id"))
                return true;
        }
        return false;
    }

    /**
     * Notmalize URL
     * @param s String. The url to normalize
     * @return String. A URL normalized
     */
    public static String normalizeUri(String s) {
        String r = StringUtils.stripAccents(s);
        r = r.replace(" ", "_");
        r = r.replaceAll("[^\\.A-Za-z0-9_]", "");
        return r;
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = false;
        if (email != null) {
            try {
                InternetAddress emailAddr = new InternetAddress(email);
                emailAddr.validate();
                result = true;
            } catch (AddressException ex) {
                result = false;
            }
        }
        return result;
    }


}



