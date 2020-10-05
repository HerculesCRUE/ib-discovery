package es.um.asio.service.comparators.strings;

public abstract class Similarity {


    public static boolean equal(String str1, String str2) {
        return str1.equals(str2);
    }

    public abstract float calculateSimilarity(String a, String b);
}
