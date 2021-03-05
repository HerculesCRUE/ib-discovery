package es.um.asio.service.comparators.strings;

/**
 * This class implements the similitude of Smith Weterman Gotoh Similarity algorithm to compare Strings
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface Similarity {
    /**
     *
     *  This abstract method calculate the similarity of the String A and B
     * @param str1 : fist sting to compare similarity
     * @param str2: second String to compare Similarity
     * @return float as similitude measure in range (0,1)
     */
    public float calculateSimilarity(String a, String b);
}
