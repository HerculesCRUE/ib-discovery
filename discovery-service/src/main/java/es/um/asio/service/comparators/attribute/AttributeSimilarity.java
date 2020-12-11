package es.um.asio.service.comparators.attribute;

import es.um.asio.service.comparators.aggregators.AccordSimilarity;
import es.um.asio.service.comparators.entities.SimilarityValue;
import es.um.asio.service.util.Utils;

import java.util.*;

public class AttributeSimilarity {

    public static SimilarityValue compareInteger(int a1, int a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            float max = Float.valueOf(Math.max(a1, a2));
            float min = Float.valueOf(Math.min(a1, a2));
            float nMax = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(max) / max) * 10));
            float nMin = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(min) / max) * 10));
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight);
    }

    public static SimilarityValue compareLong(long a1, long a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            float max = Float.valueOf(Math.max(a1, a2));
            float min = Float.valueOf(Math.min(a1, a2));
            float nMax = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(max) / max) * 10));
            float nMin = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(min) / max) * 10));
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight);
    }

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
        return new SimilarityValue(similarity,weight);
    }

    public static SimilarityValue compareDouble(double a1, double a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            float max = Float.valueOf((float) Math.max(a1, a2));
            float min = Float.valueOf((float) Math.min(a1, a2));
            float nMax = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(max) / max) * 10));
            float nMin = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(min) / max) * 10));
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight);
    }

    public static SimilarityValue compareFloat(float a1, float a2, float weight) {
        float similarity = 0;
        if (a1 == a2) {
            similarity = 1;
        } else if (weight>0.95) {
            similarity = 0;
        } else {
            float max = Float.valueOf(Math.max(a1, a2));
            float min = Float.valueOf(Math.min(a1, a2));
            float nMax = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(max) / max) * 10));
            float nMin = (max == 0) ? 0 : Float.valueOf((float) Math.floor((Double.valueOf(min) / max) * 10));
            similarity = (float) Math.pow((0.5f), (nMax - nMin));
        }
        return new SimilarityValue(similarity,weight);
    }

    public static SimilarityValue compareString(String a1, String a2, float weight) {
        float similarity = 0;
        if (a1.toLowerCase().strip().equals(a2.toLowerCase().strip())) {
            similarity = 1;
        } else {
            similarity = AccordSimilarity.calculateAccordSimilarity(a1,a2);
        }
        return new SimilarityValue(similarity,weight);
    }

    public static SimilarityValue compareBoolean(boolean a1, boolean a2, float weight) {
        float similarity = 0;
        if (a1==a2)
            similarity = 1;
        return new SimilarityValue(similarity,weight);
    }

    public static SimilarityValue compareList(List<Object> a1, List<Object> a2, float weight) {
        List<Object> l1,l2;
        Set<Integer> usedIndex = new HashSet<>();
        List<SimilarityValue> similarities = new ArrayList<>();
        if (a1.size() == 0 && a2.size() == 0) { // Si ambos tienen las listas vacías, la similaridad es 1
            similarities.add(new SimilarityValue(1f,weight));
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
                for (int j = 0; j < l1.size() ; j++) {
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
        float sumSimilarities = similarities.stream().map(s->s.getWeightedSimilarity()).reduce(0f, Float::sum);
        return new SimilarityValue(0,weight,sumSimilarities/similarities.size());
    }

    public static SimilarityValue compare(List<Object> o1,List<Object> o2, float weight) {
        Object a1,a2;
        if (o1 == null && o2 == null) {
            return new SimilarityValue(1,weight);
        }
        if ((o1 == null && o2 != null)||(o1 != null && o2 == null)) {
            return new SimilarityValue(0,weight);
        }
        if (o1.size() == 1 && o2.size() == 1) {
            a1 = o1.get(0);
            a2 = o2.get(0);
        } else {
            a1 = o1;
            a2 = o2;
        }
        Class c = getClassOffAttributes(a1,a2);
        if (c == int.class) {
            return compareInteger(Integer.parseInt(a1.toString()),Integer.parseInt(a2.toString()),weight);
        } else if (c == long.class) {
            return compareLong(Long.parseLong(a1.toString()),Long.parseLong(a2.toString()),weight);
        } else if (c == float.class) {
            return compareFloat(Float.parseFloat(a1.toString()),Float.parseFloat(a2.toString()),weight);
        } else if (c == double.class) {
            return compareDouble(Double.parseDouble(a1.toString()),Double.parseDouble(a2.toString()),weight);
        }  else if (c == boolean.class) {
            return compareBoolean(Utils.getBoolean(a1.toString()),Utils.getBoolean(a2.toString()),weight);
        } else if (c == Date.class) {
            return compareDate( getDate(a1),getDate(a2),weight);
        } else if (c == List.class) {
            return compareList((List<Object>) a1,(List<Object>) a2,weight);
        } else {
            return compareString((String) a1,(String) a2,weight);
        }
    }


    public static boolean isNumber(Object o) {
        return Utils.isValidNumber(o.toString());
    }

    public static boolean isBoolean(Object o) {
        return Utils.isBoolean(o.toString());
    }

    public static boolean isDate(Object o) {
        return Utils.isDate(o.toString());
    }

    public static Date getDate(Object o) {
        return Utils.getDate(o.toString());
    }

    public static Class getClassOffAttributes(Object a1, Object a2) {
        if (isNumber(a1) && isNumber(a2)) {
            if (Utils.checkIfInt(a1.toString()) && Utils.checkIfInt(a2.toString()))
                return int.class;
            else if (Utils.checkIfLong(a1.toString()) && Utils.checkIfLong(a2.toString()))
                return long.class;
            else if (Utils.checkIfFloat(a1.toString()) && Utils.checkIfFloat(a2.toString()))
                return float.class;
            else if (Utils.checkIfDouble(a1.toString()) && Utils.checkIfDouble(a2.toString()))
                return double.class;
            else
                return double.class;
        } else if (isBoolean(a1) && isBoolean(a2)) {
            return boolean.class;
        } else if (isDate(a1) && isDate(a2)) {
            return Date.class;
        } else if (a1 instanceof List && a2 instanceof List) {
            return List.class;
        } else {
            return String.class;
        }
    }
}

class SpecialComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 != null) return -1;
        else if (o1 != null && o2 == null) return 1;
        else if (o1 == null && o2 == null) return 0;

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