package es.um.asio.service.service;

/**
 * TextHandlerService interface. For remove stop words
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface TextHandlerService {

    /**
     * Remove the stop words of the String using dictionaries in languages
     * @param s String. The text
     * @return String. The text without stop words
     */
    public String removeStopWords(String s);
}
