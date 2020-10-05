package data;

import org.apache.commons.lang3.RandomStringUtils;
import org.javatuples.Pair;

import java.util.*;


public class SyntheticData {

    public static SyntheticData instance;
    int number = 1000;
    List<Pair<String,String>> equals, shuffled, changedCharacters, truncatedCharacter, different, allModifications, allChanges, randomCharacters;

    public static SyntheticData getInstance() {
        if (instance == null) {
            instance = new SyntheticData();
        }
        return instance;
    }

    private SyntheticData() {
        equals = new ArrayList<>();
        shuffled = new ArrayList<>();
        changedCharacters = new ArrayList<>();
        truncatedCharacter = new ArrayList<>();
        allModifications = new ArrayList<>();
        allChanges= new ArrayList<>();
        randomCharacters = new ArrayList<>();

        different = new ArrayList<>();
        // populateEquals
        for (int i = 0; i < number ;i++) {
            equals.add(generateEqualString(getRandomNumber(3,6), getRandomNumber(6,12)));
        }
        // populate Shuffled
        for (int i = 0; i < number ;i++) {
            shuffled.add(generateShuffleString(getRandomNumber(3,6), getRandomNumber(6,12)));
        }
        allModifications.addAll(shuffled);

        // populate Changed Character
        for (int i = 0; i < number ;i++) {
            int charRnd = getRandomNumber(6,12);
            int changedChars = getRandomNumber(2,charRnd/2);
            changedCharacters.add(changeCaracters(charRnd, charRnd, changedChars));
        }
        allModifications.addAll(changedCharacters);

        // Populate truncatedCharacter
        for (int i = 0; i < number ;i++) {
            truncatedCharacter.add(truncateCharacters(getRandomNumber(3,6),getRandomNumber(6,12),getRandomNumber(1,3),getRandomNumber(1,3)%2==0));
        }
        allModifications.addAll(truncatedCharacter);

        // Populate all Changes
        for (int i = 0; i < number ;i++) {
            allChanges.add(generateAllModificationsString(getRandomNumber(3,6),getRandomNumber(6,12),getRandomNumber(1,3),getRandomNumber(0,2),getRandomNumber(1,3)%2==0));
        }
        allModifications.addAll(allChanges);

        // Populate different
        for (int i = 0; i < number ;i++) {
            different.add(generateDifferentString(getRandomNumber(3,6),getRandomNumber(6,12)));
        }
    }

    public List<Pair<String, String>> getEquals() {
        return equals;
    }

    public List<Pair<String, String>> getShuffled() {
        return shuffled;
    }

    public List<Pair<String, String>> getChangedCharacters() {
        return changedCharacters;
    }

    public List<Pair<String, String>> getTruncatedCharacter() {
        return truncatedCharacter;
    }

    public List<Pair<String, String>> getDifferent() {
        return different;
    }

    public List<Pair<String, String>> getAllModifications() {
        return allModifications;
    }

    public List<Pair<String, String>> getAllChanges() {
        return allChanges;
    }

    public List<Pair<String, String>> getRandomCharacters() {
        return randomCharacters;
    }

    public Pair generateEqualString(int nWords, int nCharacters) {
        StringBuffer generatedString = new StringBuffer();
        for (int i = 0; i< nWords; i++) {
            if (generatedString.length()>0)
                generatedString.append(" ");
            generatedString.append(RandomStringUtils.randomAlphabetic(nCharacters));
        }
        return new Pair<String,String>(generatedString.toString(), generatedString.toString());
    }

    public Pair generateDifferentString(int nWords, int nCharacters) {
        StringBuffer generatedString1 = new StringBuffer();
        StringBuffer generatedString2 = new StringBuffer();
        for (int i = 0; i< nWords; i++) {
            if (generatedString1.length()>0)
                generatedString1.append(" ");
            generatedString1.append(RandomStringUtils.randomAlphabetic(nCharacters));
            if (generatedString2.length()>0)
                generatedString2.append(" ");
            generatedString2.append(RandomStringUtils.randomAlphabetic(nCharacters));
        }
        return new Pair<String,String>(generatedString1.toString(), generatedString2.toString());
    }

    public Pair generateShuffleString(int nWords, int nCharacters) {
        Pair<String,String> t = generateEqualString(nWords, nCharacters);
        List<String> words = Arrays.asList(t.getValue(1).toString().split(" "));
        Collections.shuffle(words);
        String[] arrayWords = words.toArray(new String[words.size()]);
        Pair<String,String> tAux = new Pair(t.getValue(0), String.join(" ", arrayWords));
        if (tAux.getValue(0).toString().equals(tAux.getValue(1).toString()))
            generateEqualString(nWords,nCharacters);
        t = new Pair(t.getValue(0), String.join(" ", arrayWords));
        if (t.getValue(0).equals(t.getValue(1)))
            return generateShuffleString(nWords, nCharacters);
        return t;
    }

    public Pair changeCaracters(int nWords, int nCharacters, int nChangedCharacter) {
        Pair<String,String> t = generateEqualString(nWords, nCharacters);
        List<String> words = new ArrayList<>();
        for (String word : Arrays.asList(t.getValue(1).toString().split(" "))) {
            for (int i = 0; i < nChangedCharacter; i++) {
                int pos = getRandomNumber(0, word.length());
                char c = 0;
                do {
                    c = rndChar();
                } while (c == 0 || c == word.charAt(pos));
                word = word.substring(0, pos) + rndChar() + word.substring(pos + 1);
            }
            words.add(word);
        }
        String[] arrayWords = words.toArray(new String[words.size()]);
        return new Pair(t.getValue(0), String.join(" ", arrayWords));
    }

    public Pair truncateCharacters(int nWords, int nCharacters, int nTruncatedCharacter, boolean truncateAll) {
        Pair<String,String> t = generateEqualString(nWords, nCharacters);
        List<String> words = new ArrayList<>();
        if (truncateAll) {
            for (String word : Arrays.asList(t.getValue(1).toString().split(" "))) {
                words.add(word.substring(0,word.length() - nTruncatedCharacter));
            }
        } else {
            int index = getRandomNumber(0,nWords);
            int pos = 0;
            for (String word : Arrays.asList(t.getValue(1).toString().split(" "))) {
                if (pos == index)
                    words.add(word.substring(0,word.length() - nTruncatedCharacter));
                else
                    words.add(word);
                pos++;
            }
        }
        String[] arrayWords = words.toArray(new String[words.size()]);
        return new Pair(t.getValue(0), String.join(" ", arrayWords));
    }

    public Pair generateAllModificationsString(int nWords, int nCharacters, int nChangedCharacter, int nTruncatedCharacter, boolean truncateAll) {
        Pair<String,String> t = generateEqualString(nWords, nCharacters);
        List<String> words = Arrays.asList(t.getValue(1).toString().split(" "));
        Collections.shuffle(words);
        String[] arrayWords = words.toArray(new String[words.size()]);
        Pair<String,String> tAux = new Pair(t.getValue(0), String.join(" ", arrayWords));
        if (tAux.getValue(0).toString().equals(tAux.getValue(1).toString()))
            generateEqualString(nWords,nCharacters);
        t = new Pair(t.getValue(0), String.join(" ", arrayWords));

        List<String> wordsToChange = new ArrayList<>();
        for (String word : Arrays.asList(t.getValue(1).toString().split(" "))) {
            for (int i = 0; i < nChangedCharacter; i++) {
                int pos = getRandomNumber(0, word.length());
                char c = 0;
                do {
                    c = rndChar();
                } while (c == 0 || c == word.charAt(pos));
                word = word.substring(0, pos) + rndChar() + word.substring(pos + 1);
            }
            wordsToChange.add(word);
        }
        String[] arrayWords2 = wordsToChange.toArray(new String[words.size()]);
        t = new Pair(t.getValue(0), String.join(" ", arrayWords2));

        List<String> wordsToTruncate = new ArrayList<>();
        if (truncateAll) {
            for (String word : Arrays.asList(t.getValue(1).toString().split(" "))) {
                wordsToTruncate.add(word.substring(0,word.length() - nTruncatedCharacter));
            }
        } else {
            int index = getRandomNumber(0,nWords);
            int pos = 0;
            for (String word : Arrays.asList(t.getValue(1).toString().split(" "))) {
                if (pos == index)
                    wordsToTruncate.add(word.substring(0,word.length() - nTruncatedCharacter));
                else
                    wordsToTruncate.add(word);
                pos++;
            }
        }
        String[] arrayWords3 = wordsToTruncate.toArray(new String[words.size()]);

        return new Pair(t.getValue(0), String.join(" ", arrayWords3));
    }

    public Pair randomString(int nWords, int nCharacters) {
        Pair<String,String> t = generateEqualString(nWords, nCharacters);
        List<String> words = new ArrayList<>();
        for (String word : Arrays.asList(t.getValue(0).toString().split(" "))) {
            StringBuffer w = new StringBuffer();
            for (int i = 0 ; i < word.length() ; i++) {
                w.append(String.valueOf(rndChar()));
            }
            words.add(w.toString());
        }
        String[] arrayWords = words.toArray(new String[words.size()]);
        return new Pair(t.getValue(0), String.join(" ", arrayWords));
    }

    public int getRandomNumber(int min, int max) {
        if (min > max)
            max = min;
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    private static char rndChar () {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);
    }
}
