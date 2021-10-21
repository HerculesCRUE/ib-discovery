package es.um.asio.service.comparators.attribute;

import es.um.asio.service.comparators.aggregators.AccordSimilarity;
import es.um.asio.service.comparators.entities.SimilarityValue;
import es.um.asio.service.util.Utils;

import java.util.*;

/**
 * This class implements the similarity measure for Attribute
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class AttributeSimilarity {

    /**
     * Constants by type
     */
    private static final String INTEGER = "int";
    private static final String LONG = "long";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String BOOLEAN = "boolean";
    private static final String DATE = "Date";
    private static final String LIST = "List";
    private static final String LINK = "Link";
    private static final String STRING = "String";

    /**
     * Default constructor
     */
    private AttributeSimilarity() {}


    /**
     * The algorithm implements the integer comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#atributos-de-tipo-num%C3%A9rico"
     * @see SimilarityValue
     * @param a1 The first integer
     * @param a2 The second integer
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareInteger(int a1, int a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            double max = (double) Math.max(a1, a2);
            double min = (double) Math.min(a1, a2);
            float nMax = (max == 0) ? 0 : (float) Math.floor(1f * 10f);
            float nMin = (max == 0) ? 0 : (float) Math.floor((min / max) * 10f);
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight,false);
    }

    /**
     * The algorithm implements the integer comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#atributos-de-tipo-num%C3%A9rico"
     * @see SimilarityValue
     * @param a1 The first Long
     * @param a2 The second Long
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareLong(long a1, long a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            double max = (double) (Math.max(a1, a2));
            double min = (double)  (Math.min(a1, a2));
            float nMax = (max == 0) ? 0 : (float) Math.floor(1f * 10f);
            float nMin = (max == 0) ? 0 : (float) Math.floor((min / max) * 10f);
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight,false);
    }

    /**
     * The algorithm implements the Date comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param a1 The first Long
     * @param a2 The second Long
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareDate(Date a1, Date a2, float weight) {
        float similarity = 0;
        if (a1.compareTo(a2) == 0) {
            similarity = 1;
        } else {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(a1);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(a2);
            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
                similarity = 1;
            } else {
                similarity = 0;
            }
        }
        return new SimilarityValue(similarity,weight,false);
    }

    /**
     * The algorithm implements the Double comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param a1 The first Double
     * @param a2 The second Double
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareDouble(double a1, double a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            float max = (float) Math.max(a1, a2);
            float min = (float) Math.min(a1, a2);
            float nMax = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(max) / max) * 10));
            float nMin = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(min) / max) * 10));
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight,false);
    }

    /**
     * The algorithm implements the Float comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param a1 The first Float
     * @param a2 The second Float
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareFloat(float a1, float a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            float max = (Math.max(a1, a2));
            float min = (Math.min(a1, a2));
            float nMax = (max == 0) ? 0 : (float) ((float) Math.floor((Double.valueOf(max) / max) * 10));
            float nMin = (max == 0) ? 0 : (float) ((float) Math.floor((Double.valueOf(min) / max) * 10));
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight,false);
    }

    /**
     * The algorithm implements the String comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param a1 The first String
     * @param a2 The second String
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareString(String a1, String a2, float weight) {
        float similarity = 0;
        if (a1.toLowerCase().strip().equals(a2.toLowerCase().strip()) || (!Utils.isValidString(a1) || !Utils.isValidString(a2) )) {
            similarity = 1;
        } else {
            similarity = AccordSimilarity.calculateAccordSimilarity(a1,a2);
        }
        return new SimilarityValue(similarity,weight,false);
    }

    /**
     * The algorithm implements the String comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param a1 The first String
     * @param a2 The second String
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareLink(String a1, String a2, float weight) {
        float similarity = 0;
        if (a1.toLowerCase().strip().equals(a2.toLowerCase().strip())) {
            similarity = 1;
        } else {
            similarity = AccordSimilarity.calculateAccordSimilarity(a1,a2);
        }
        return new SimilarityValue(similarity,weight,true);
    }

    /**
     * The algorithm implements the Boolean comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param a1 The first Boolean
     * @param a2 The second Boolean
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareBoolean(boolean a1, boolean a2, float weight) {
        float similarity = 0;
        if (a1==a2)
            similarity = 1;
        return new SimilarityValue(similarity,weight,false);
    }

    /**
     * The algorithm implements the List comparision
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param a1 The first List
     * @param a2 The second List
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compareList(List<Object> a1, List<Object> a2, float weight) { // Mirar
        List<Object> l1;
        List<Object> l2;
        boolean isLink = false;
        Set<Integer> usedIndex = new HashSet<>();
        List<SimilarityValue> similarities = new ArrayList<>();
        if (a1.isEmpty() && a2.isEmpty()) { // Si ambos tienen las listas vacías, la similaridad es 1
            similarities.add(new SimilarityValue(1f,weight,false));
        } else { // Si no estan vacias
            if (a1.size()>=a2.size()) { // Pongo primero la lista mayor
                l1 = a1;
                l2 = a2;
            } else {
                l1 = a2;
                l2 = a1;
            }
            Collections.sort(l1, new SpecialComparator());
            Collections.sort(l2, new SpecialComparator());

            for (int i = 0; i < l1.size() ; i++) {
                int maxSimilarityIndex = Integer.MIN_VALUE;
                SimilarityValue maxSimilarity = null;
                for (int j = 0; j < l2.size() ; j++) {
                    if (!isLink) {
                        isLink = Utils.isValidURL((String) l1.get(i)) || Utils.isValidURL((String) l2.get(i));
                    }
                    if (!usedIndex.contains(j)) {
                        SimilarityValue sv = compare(Arrays.asList(l1.get(i)),Arrays.asList(l2.get(j)),weight);
                        if (maxSimilarity == null || (sv.getWeightedSimilarity() > maxSimilarity.getWeightedSimilarity()) ) {
                            maxSimilarity = sv;
                            maxSimilarityIndex = j;
                        }
                        if (maxSimilarity.getSimilarity() == 1f)
                            break;
                    }
                }
                if (maxSimilarity!=null) {
                    similarities.add(maxSimilarity);
                    usedIndex.add(maxSimilarityIndex);
                }
            }
        }
        // Agrego las similitudes
        float sumSimilarities = similarities.stream().map(SimilarityValue::getWeightedSimilarity).reduce(0f, Float::sum);
        return new SimilarityValue(0,weight,sumSimilarities/similarities.size(), isLink);
    }

    /**
     * The generic algorithm resolve the the type of the parameters and apply the accuracy comparator
     * @see "https://github.com/HerculesCRUE/ib-discovery/blob/master/docs/ASIO_Libreria_de_descubrimiento.md#m%C3%A9tricas-de-similitud-para-atributos"
     * @see SimilarityValue
     * @param o1 The first List of Objects (of any type).
     * @param o2 The second List of Objects (of any type).
     * @param weight The weight tho apply
     * @return SimilarityValue
     */
    public static SimilarityValue compare(List<Object> o1,List<Object> o2, float weight) {
        Object a1;
        Object a2;
        if (o1 == null && o2 == null) {
            return new SimilarityValue(1,weight,false);
        } else if ((o1 == null ) || (o2 == null)) {
            return new SimilarityValue(1,weight,false); // Cuidado
        }
        if (o1.size() == 1 && o2.size() == 1) {
            a1 = o1.get(0);
            a2 = o2.get(0);
        } else {
            a1 = o1;
            a2 = o2;
        }
        String c = getClassOffAttributes(a1,a2);
        if (c.equalsIgnoreCase(INTEGER)) {
            return compareInteger(Integer.parseInt(a1.toString()),Integer.parseInt(a2.toString()),weight);
        } else if (c.equalsIgnoreCase(LONG)) {
            return compareLong(Long.parseLong(a1.toString()),Long.parseLong(a2.toString()),weight);
        } else if (c.equalsIgnoreCase(FLOAT)) {
            return compareFloat(Float.parseFloat(a1.toString()),Float.parseFloat(a2.toString()),weight);
        } else if (c.equalsIgnoreCase(DOUBLE)) {
            return compareDouble(Double.parseDouble(a1.toString()),Double.parseDouble(a2.toString()),weight);
        }  else if (c.equalsIgnoreCase(BOOLEAN)) {
            return compareBoolean(Utils.getBoolean(a1.toString()),Utils.getBoolean(a2.toString()),weight);
        } else if (c.equalsIgnoreCase(DATE)) {
            return compareDate( getDate(a1),getDate(a2),weight);
        } else if (c.equalsIgnoreCase(LIST)) {
            return compareList((List<Object>) a1,(List<Object>) a2,weight);
        } else if (c.equalsIgnoreCase(LINK)) {
            return compareLink((String) a1,(String) a2,weight);
        } else {
            return compareString((String) a1,(String) a2,weight);
        }
    }

    /**
     * The function determine if the object pass in parameter is a Number
     * @param o : Object. The object to evaluate
     * @return boolean. True if object is of type, else false
     */
    public static boolean isNumber(Object o) {
        return Utils.isValidNumber(o.toString());
    }

    /**
     * The function determine if the object pass in parameter is a Boolean
     * @param o : Object. The object to evaluate
     * @return boolean. True if object is of type, else false
     */
    public static boolean isBoolean(Object o) {
        return Utils.isBoolean(o.toString());
    }

    /**
     * The function determine if the object pass in parameter is a Date Object
     * @param o : Object. The object to evaluate
     * @return boolean. True if object is of type, else false
     */
    public static boolean isDate(Object o) {
        return Utils.isDate(o.toString());
    }

    /**
     * The function return a Date if object is a Date type, null else
     * @param o : Object. The object to evaluate
     * @return Date. A valid date if object is of type, else null
     */
    public static Date getDate(Object o) {
        return Utils.getDate(o.toString());
    }

    /**
     * The function return the common type of the params objects
     * @param a1 : Object. The first object
     * @param a2 : Object. The second object
     * @return String. The common type of the params
     */
    public static String getClassOffAttributes(Object a1, Object a2) {
        if (isNumber(a1) && isNumber(a2)) {
            if (Utils.checkIfInt(a1.toString()) && Utils.checkIfInt(a2.toString()))
                return INTEGER;
            else if (Utils.checkIfLong(a1.toString()) && Utils.checkIfLong(a2.toString()))
                return LONG;
            else if (Utils.checkIfFloat(a1.toString()) && Utils.checkIfFloat(a2.toString()))
                return FLOAT;
            else if (Utils.checkIfDouble(a1.toString()) && Utils.checkIfDouble(a2.toString()))
                return DOUBLE;
            else
                return DOUBLE;
        } else if (isBoolean(a1) && isBoolean(a2)) {
            return BOOLEAN;
        } else if (isDate(a1) && isDate(a2)) {
            return DATE;
        } else if (a1 instanceof List && a2 instanceof List) {
            return LIST;
        } else if (Utils.isValidURL(a1.toString()) && Utils.isValidURL(a1.toString())) {
            return LINK;
        } else {
            return STRING;
        }
    }
}

/**
 * This class implements a SpecialComparator
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
class SpecialComparator implements Comparator<Object> {

    /**
     * Comparator for Objects for sort in list
     * @param o1 First Object to compare
     * @param o2 Second Object to compare
     * @return int, -1 if o1 is less to o2, 0 if are equals or 1 if o1 is greater to o2
     */
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 != null) return -1;
        else if (o1 != null && o2 == null) return 1;
        else if (o1 == null) return 0;

        else {
            Class<?> c1 = o1.getClass();
            Class<?> c2 = o2.getClass();

            if (Number.class.isAssignableFrom(c1) && String.class.isAssignableFrom(c2)){
                return -1;
            }else if (Number.class.isAssignableFrom(c2) && String.class.isAssignableFrom(c1)){
                return 1;
            }
            else if (Number.class.isAssignableFrom(o1.getClass()) && Number.class.isAssignableFrom(o2.getClass())){
                double d = ((Number)o1).doubleValue() - ((Number)o2).doubleValue();
                if (Math.abs(d)<1e-8) return 0;
                else return (d<0) ? -1 : 1;
            }
            else {
                return o1.toString().compareTo(o2.toString());
            }
        }
    }
}