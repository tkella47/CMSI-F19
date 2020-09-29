package spellex;

import java.util.*;
/**
 * SpellEx uses dictionaries to check spellings of words
 * @author Thomas Kelly
 * @date March 5,2020
 */
public class SpellEx {
    /**
     * Candidate is a custom class designed to make a priority queue easy to implement.
     * Implements a compareTo method ordered by edit distance, frequency, then ascending word order
     * @author Thomas Kelly
     *
     */
    
    private class Candidate implements Comparable<Candidate>{
        private int freq = Integer.MIN_VALUE;
        private String word = "";
        private int editD = Integer.MAX_VALUE;
    /**
     * Constructor for Candidate    
     * @param editD - edit distance
     * @param freq - frequency of word
     * @param word - word of Candidate
     */
        public Candidate(int editD, int freq, String word) {
            this.editD = editD;
            this.freq = freq;
            this.word= word;
        }
        /**
         * compares two candidates
         * returns 1 if candidate has a greater edit distance, lower frequency, 
         * or if the word is first in the alphabet
         * returns -1  if candidate has a lesser edit distance, higher frequency, or if the word is last in the alphabet
         * returns 0 if candidates are equal in words
         */
        public int compareTo(Candidate other) {
            if(this.word.equals(other.word)) {
                return 0;
            }
            
            if(this.editD > other.editD) {
                return 1;
            } else if(this.editD < other.editD) {
                return -1;
            } else if(this.freq > other.freq ) {
                return -1;
            } else if (this.freq < other.freq) {
                return 1;
            } else {
                int index = 0;
                while(this.word.charAt(index) == other.word.charAt(index)) {
                    index++;
                    if(this.word.length() - 1 == index) {
                        return -1;
                    }
                    if(other.word.length() - 1 == index) {
                        return 1;
                    }
                }
                for(int i = 0; i < LETTERS.length; i++) {
                    if (LETTERS[i] == this.word.charAt(index)) {
                        return -1;
                    }
                    if  (LETTERS[i] == other.word.charAt(index)) {
                        return 1;
                    }
                }
            }
            return 0;
        }
    }
    /**
     * FreqCandidate is similar to Candidate, except the compare method is ordered by frequency then by alphabet
     * @author tommy
     *
     */
    private class FreqCandidate implements Comparable<FreqCandidate>{
        private int freq = Integer.MIN_VALUE;
        private String word = "";
        private int editD = Integer.MAX_VALUE;
        /**
         * Constructor for FreqCandidate    
         * @param editD - edit distance
         * @param freq - frequency of word
         * @param word - word of Candidate
         */
        public FreqCandidate(int editD, int freq, String word) {
            this.editD = editD;
            this.freq = freq;
            this.word= word;
        }
        /**
         * compares two candidates
         * returns 1 if candidate has lower frequency, 
         * or if the word is first in the alphabet
         * returns -1  if candidate has a higher frequency, or if the word is last in the alphabet
         * returns 0 if candidates are equal in words
         */
        public int compareTo(FreqCandidate other) {
            if(this.word.equals(other.word)) {
                return 0;
            }
            if(this.freq > other.freq ) {
                return -1;
            } else if (this.freq < other.freq) {
                return 1;
            } else {
                int index = 0;
                while(this.word.charAt(index) == other.word.charAt(index)) {
                    index++;
                    if(this.word.length() - 1 == index) {
                        return -1;
                    }
                    if(other.word.length() - 1 == index) {
                        return 1;
                    }
                }
                for(int i = 0; i < LETTERS.length; i++) {
                    if (LETTERS[i] == this.word.charAt(index)) {
                        return -1;
                    }
                    if  (LETTERS[i] == other.word.charAt(index)) {
                        return 1;
                    }
                }
            }
            return 0;
        }
    }
    public static boolean debug = false;
    // Note: Not quite as space-conscious as a Bloom Filter,
    // nor a Trie, but since those aren't in the JCF, this map 
    // will get the job done for simplicity of the assignment
    private Map<String, Integer> dict;
    
    // For your convenience, you might need this array of the
    // alphabet's letters for a method
    
    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Constructs a new SpellEx spelling corrector from a given
     * "dictionary" of words mapped to their frequencies found
     * in some corpus (with the higher counts being the more
     * prevalent, and thus, the more likely to be suggested)
     * @param words The map of words to their frequencies
     */
    SpellEx(Map<String, Integer> words) {
        dict = new HashMap<>(words);
    }
    
    /**
     * Returns the edit distance between the two input Strings
     * s0 and s1 based on the minimal number of insertions, deletions,
     * replacements, and transpositions required to transform s0
     * into s1
     * @param s0 A "start" String
     * @param s1 A "destination" String
     * @return The minimal edit distance between s0 and s1
     */
    public static int editDistance (String s0, String s1) {
        int[][] table = new int[s0.length()+1][s1.length()+1];
        for(int i = 0; i <s0.length() + 1; i++) {
            table[i][0] = i;
        }
        for(int i = 0; i <s1.length() + 1; i++) {
            table[0][i] = i;
        }
        for(int i =1; i < s0.length() + 1; i++) {
            for(int j = 1; j < s1.length() + 1; j++) {
                table[i][j] = Math.min(table[i][j-1] + 1, Math.min(table[i-1][j] + 1, Math.min(table[i-1][j-1]  + (s0.charAt(i-1) == s1.charAt(j-1) ? 0 : 1), (i>=2 && j>=2 ? s0.charAt(i - 1) == s1.charAt(j-2) && s0.charAt(i-2) == s1.charAt(j-1) ? table[i-2][j-2] + 1 : Integer.MAX_VALUE : Integer.MAX_VALUE))));
            }
        }
        return table[s0.length()][s1.length()];
    }
    
    /**
     * Returns the n closest words in the dictionary to the given word,
     * where "closest" is defined by:
     * <ul>
     *   <li>Minimal edit distance (with ties broken by:)
     *   <li>Largest count / frequency in the dictionary (with ties broken by:)
     *   <li>Ascending alphabetic order
     * </ul>
     * @param word The word we are comparing against the closest in the dictionary
     * @param n The number of least-distant suggestions desired
     * @return A set of up to n suggestions closest to the given word
     */
    public Set<String> getNLeastDistant (String word, int n) {
    
        Set<String> output = new HashSet<String>();
        PriorityQueue<Candidate> best = new PriorityQueue<Candidate>();
        for (Map.Entry<String, Integer> dictMap : dict.entrySet()) {
                best.add(new Candidate(editDistance(word,dictMap.getKey()),dictMap.getValue(),dictMap.getKey()));
        }
        while(!best.isEmpty() && n > 0) {
            //print("org word: " + word + " sug " + best.peek().word + " edit " + best.peek().editD + " freq " + best.peek().freq);
            output.add(best.remove().word);
            n--;
        }
        return output;
    }
    
    /**
     * Returns the set of n most frequent words in the dictionary to occur with
     * edit distance distMax or less compared to the given word. Ties in
     * max frequency are broken with ascending alphabetic order.
     * @param word The word to compare to those in the dictionary
     * @param n The number of suggested words to return
     * @param distMax The maximum edit distance (inclusive) that suggested / returned 
     * words from the dictionary can stray from the given word
     * @return The set of n suggested words from the dictionary with edit distance
     * distMax or less that have the highest frequency.
     */
    public Set<String> getNBestUnderDistance (String word, int n, int distMax) {
        Set<String> modDict = new HashSet<String>();
        String modWord = "_";
        for(int i = 0; i < word.length(); i++) {
            modWord += word.charAt(i) + "_";
            }
       //insert
        for(int i = 0; i < modWord.length() ; i += 2) {
            modDict = insert(modWord,i,modDict);
        }
        //delete
        modDict = delete(word,modDict);
        //replacement
        modDict = replacement(word,modDict);
        //transpose
        modDict = transpose(word,modDict);
        Set<String> bigModDict = new HashSet<String>();
        bigModDict.addAll(modDict);
        Iterator<String> it = modDict.iterator();
        if(distMax > 1) {
            while(it.hasNext()) {
                String newWord = it.next();
                modWord = "_";
                for(int i = 0; i < newWord.length(); i++) {
                    modWord += newWord.charAt(i) + "_";
                }
           //insert
                for(int i = 0; i < modWord.length() ; i += 2) {
                    bigModDict = insert(modWord,i,bigModDict);
                }
            //delete
                    bigModDict = delete(newWord,bigModDict);
            //replacement
                    bigModDict = replacement(newWord,bigModDict);
            //transpose
                    bigModDict = transpose(newWord, bigModDict);
            }
            modDict.clear();
            modDict.addAll(bigModDict);
            
        }
        Iterator<String> iter = modDict.iterator();
        PriorityQueue<FreqCandidate> best = new PriorityQueue<FreqCandidate>();
        while(iter.hasNext()) {
            String tempWord = iter.next();
            if(dict.containsKey(tempWord)) {
                best.add(new FreqCandidate(distMax,dict.get(tempWord),tempWord));
            }
        }
        Set<String> result = new HashSet<String>();
        while(n > 0 && !best.isEmpty()) {
            print("word that is found " + best.peek().word + " with freq of " + best.peek().freq);
            result.add(best.peek().word);
            best.remove();
            n--;
        }
        return result;
    }
    /**
     * insert
     * @param word this is the word with underscores to make insertion easier.
     * @param index this is the location that a character should be inserted
     * @param result the returning Set of new words without underscores
     * @return result  the Set of new words
     */
    public Set<String> insert (String word, int index, Set<String> result){
       
        String modWord = "";
        for(int i = 0; i < LETTERS.length; i++) {
            for(int j = 0; j < word.length(); j++) {
                if(j==index) {
                    modWord+=LETTERS[i];
                } else if(j % 2 != 0 ) {
                    modWord+=word.charAt(j);
                }
            }
            result.add(modWord);
            modWord = "";
        }
        return result;   
    }
    /**\
     * delete
     * @param word : The word without underscores
     * @param result deletion occurs at each position on the word
     * @return result : the new set of words
     */
    public Set<String> delete (String word, Set<String> result) {
        String modWord = "";
        for(int i = 0; i < word.length(); i++) {
            for(int j = 0; j < word.length(); j++) {
                if(j != i) {
                    modWord+= word.charAt(j);
                }
            }
            result.add(modWord);
            modWord = "";
        }
        return result;
    }
    /**
     * replacement
     * @param word : the original word
     * @param result : A set of strings with swapped characters
     * @return the set of new words
     */
    public Set<String> replacement(String word, Set<String> result) {
        String modWord = "";
        for(int i = 0; i < LETTERS.length; i++) {
            for(int j = 0; j < word.length(); j++) {
                for(int k = 0; k < word.length(); k++) {
                    if(k == j) {
                       modWord += LETTERS[i]; 
                    } else {
                        modWord += word.charAt(k);
                    }
                }
                result.add(modWord);
                modWord = "";
            }
        }
        return result;
    }
    /**
     * transpose
     * @param word : the original word 
     * @param result : the set of Strings with characters swapped 
     * @return set of new words
     */
    public Set<String> transpose (String word, Set<String> result) {
        String modWord = "";
        for(int i = 0; i < word.length() - 1; i++) {
            for(int j = 0; j < word.length(); j++) {
                if( j == i) {
                    modWord += word.charAt( j+1 );
                    modWord += word.charAt(j);
                    j++;
                } else {
                    modWord += word.charAt(j);
                }
            }
            result.add(modWord);
            modWord = "";
        }
        return result;
    }
    /**
     * Debug print method
     * @param s
     */
    public static void print(Object s) {
        if(debug) {
            System.out.println(s);
        }
    }
    /**
     * Debug print method
     * @param s
     */
    public static void printA(Object s) {
        System.out.println(s);
    }
    /*
    public Set<String> getNBestUnderDistance2 (String word, int n, int distMax) {
        PriorityQueue<FreqCandidate> best = new PriorityQueue<FreqCandidate>();
        Set<String> output = new HashSet<String>();
        
        for (Map.Entry<String, Integer> dictMap : dict.entrySet()) {
            if(!(dictMap.getKey().length() > word.length() + distMax)) {
                if(editDistance(word,dictMap.getKey()) <= distMax) {
                    best.add(new FreqCandidate(editDistance(word,dictMap.getKey()),dictMap.getValue(),dictMap.getKey()));
                }
            }
        }
        while(!best.isEmpty() && n > 0) {
            print("org word: " + word + " sug " + best.peek().word + " edit " + best.peek().editD + " freq " + best.peek().freq);
            output.add(best.remove().word);
            n--;
        }
        return output;
        
    }*/

}

