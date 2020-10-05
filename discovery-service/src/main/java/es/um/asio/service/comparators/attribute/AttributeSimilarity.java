package es.um.asio.service.comparators.attribute;

public class AttributeSimilarity {

    public static double compare(int a1, int a2) {
        return Math.pow((0.5),Math.abs(a1-a2));
    }
}
