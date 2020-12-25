package src.rake;

import src.finalproject.Sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/*
Implementation of RAKE - Rapid Automatic Keyword Extraction algorithm
as described in:
Rose, S., D. Engel, N. Cramer, and W. Cowley (2010).
Automatic keyword extraction from individual documents.
In M. W. Berry and J. Kogan (Eds.), Text Mining: Applications and Theory.unknown: John Wiley and Sons, Ltd.

NOTE: The original code (from https://github.com/aneesha/RAKE)
has been extended by a_medelyan (zelandiya)
with a set of heuristics to decide whether a phrase is an acceptable candidate
as well as the ability to set frequency and phrase length parameters
important when dealing with longer documents

NOTE 2: The code published by a_medelyan (https://github.com/zelandiya/RAKE-tutorial)
has been additionally extended by Marco Pegoraro to implement the adjoined candidate
feature described in section 1.2.3 of the original paper. Note that this creates the
need to modify the metric for the candidate score, because the adjoined candidates
have a very high score (because of the nature of the original score metric)

NOTE 3: Python code from aneesha (https://https://github.com/zelandiya/RAKE-tutorial) is
converted to Java in the package by me.
 */
public class RakeModel {
    ArrayList<String> stopWords = new StopWords().getStopWords();
    // An adjoined string refers to a phrase i.e. 1 or more words
    public int minCharLength = 1, maxWordsLength = 5;
    // Minimum number of words in the adjoined string
    public int minWords = 1, maxWords = 1;
    // Minimum phrase and keyword frequencies required to be counted
    public int minPhraseFreqAdj = 2, minKeywordFrequency = 1;

    private boolean isNumber(String s)
    {
        return s.matches("[+-]?[0-9]*\\.?[0-9]+");
    }

    private ArrayList<String> separateWords(String text, int minWordSize)
    {
        Pattern textSplitter = Pattern.compile("[a-zA-Z0-9_\\+\\-/]");
        ArrayList<String> words = new ArrayList<>();
        for (String word: textSplitter.split(text))
        {
            String currWord = word.strip();
            // Numbers stay in phrase but are not counted as they invalidate phrase scores
            if (currWord.length() > minWordSize && currWord != "" && !isNumber(currWord))
                words.add(currWord);
        }
        return words;
    }
    private  ArrayList<String> separateSentences(String text){
        // Sentence pattern obtained from Python code
        Pattern sp = Pattern.compile("[\\\\[\\\\]\\n.!?,;:\\t\\\\-\\\\\"\\\\(\\\\)\\\\\\'\\u2019\\u2013]");
        // Convert String array to arrayList
        ArrayList<String> sentenceList = new ArrayList<>(Arrays.asList(sp.split(text)));
        return sentenceList;
    }
    private Pattern stopWordRegexBuilder(){
        String stopWordPattern = "";
        for (String word: stopWords)
            stopWordPattern.concat("\\b" + word + "\\b|");
        stopWordPattern = stopWordPattern.substring(0, stopWordPattern.length() - 1);
        return Pattern.compile(stopWordPattern);
    }
    private ArrayList<String> adjoinCandidateExtractor(ArrayList<String> sentences)
    {
        WordDict adjoinedCandidates = new WordDict();
        for (String sentence: sentences)
            adjoinedCandidates.addDict(adjoinedSentenceExtractor(sentence));
        return WordDict.adjoinedCandidateFilter(adjoinedCandidates, minPhraseFreqAdj);
    }
    private WordDict adjoinedSentenceExtractor(String sentence)
    {
        WordDict wordDict = new WordDict();
//        ArrayList<String> candidates = new ArrayList<>();
        String[] sLCase = sentence.toLowerCase().strip().split(" ");
        for (int i = minWords; i <= maxWords; i++)   // i is the number of keywords
            for (int j = 0; j < i; j++){
                if (! stopWords.contains(sLCase[j])){
                    String candidate = sLCase[j];
                    int k = 1;
                    int keywordCounter = 1;           // Measures length of candidate sequence
                    boolean containsStopWord = false;
                    while (keywordCounter < i && (j+k) < sLCase.length){
                        // Add the next word to candidate sequence
                        candidate += " " + sLCase[j+k];
                        if (stopWords.contains(sLCase[i+j])) containsStopWord = true;
                        else keywordCounter += 1;
                        k++;      // Go to next word
                    }
                    //Candidate added to list iff (1) it contains a stop word, (2) the last word is not a stop word and
                    // adjoined candidate phrase is exactly equal to the number of keywords allowed (i) - prevents duplicates
                    if (containsStopWord &&
                            !stopWords.contains(candidate.substring(candidate.lastIndexOf(" ") + 1)) &&
                            keywordCounter == i)
                        wordDict.add(candidate);
                }
            }
        return wordDict;
    }

    private ArrayList<String> generateCandidateKeywords(ArrayList<String> sentences){
        ArrayList<String> phraseList = new ArrayList<>();
        WordDict phraseCounter = new WordDict();
        for (String sentence: sentences){
            sentence = sentence.strip().replaceAll(stopWordRegexBuilder().pattern(), "|");
            String[] phrases = sentence.split("|");
            for (String phrase: phrases)
                if (phraseCheck(phrase)) phraseCounter.add(phrase);
        }
        // Add phrase to array only if it passes a minimum frequency
        for (String phrase: phraseCounter){
            if (phraseCounter.getFrequency(phrase) >= minKeywordFrequency)
                phraseList.add(phrase);
        }
        ArrayList<String> adjCandidates = adjoinCandidateExtractor(sentences);
        System.arraycopy(adjCandidates, 0, phraseList, phraseList.size(), phraseList.size());
        return phraseList;
    }
    private boolean phraseCheck(String phrase){
        if (phrase.length() < minCharLength) return  false;   // Minimum character length check
        if (phrase == "") return  false;
        if (phrase.split(" ").length > maxWordsLength) return false; // Maximum phrase length check

        int alphabets = 0, digits = 0;
        for (int i = 0; i < phrase.length(); i++){
            if (Character.isDigit(phrase.charAt(i))) digits++;
            else if (Character.isAlphabetic(phrase.charAt(i))) alphabets++;
        }
        if (alphabets == 0 || digits > alphabets) return false;     // phrase must have more alphabets than characters
        return true;
    }

    private HashMap<String, Double> calculateWordScore(ArrayList<String> phraseArray){
        WordDict wordFrequencies = new WordDict();
        WordDict wordDegree = new WordDict();
        for (String phrase: phraseArray){
            ArrayList<String> words = separateWords(phrase, 0);
            int wordsLength = words.size();
            int listDegree = wordsLength - 1;
            for (String word: words){
                wordFrequencies.add(word, 1);
                wordDegree.add(word, listDegree);
            }
        }
        for (String word: wordFrequencies)
            wordDegree.add(word, wordFrequencies.getFrequency(word));

        HashMap<String, Double> wordScores = new HashMap<>();
        for (String word: wordFrequencies){
            Double value = wordDegree.getFrequency(word) / (1.0 * wordFrequencies.getFrequency(word));
            wordScores.put(word, value);
        }
        return wordScores;
    }
    private HashMap<String, Double> getCandidateScores(ArrayList<String> phraseArray,
                                                      HashMap<String, Double> wordScores){
        HashMap<String, Double> candidateScores = new HashMap();
        for (String phrase: phraseArray){
            double cScore = 0.0;
            ArrayList<String> wordArray = separateWords(phrase, 0);
            for (String word: wordArray)
                cScore += wordScores.get(word);
            candidateScores.put(phrase, cScore);
        }
        return candidateScores;
    }
    public ArrayList<String> run(String text){
        ArrayList<String> sentences = separateSentences(text);
        ArrayList<String> phraseArray = generateCandidateKeywords(sentences);
        HashMap<String, Double> wordScores = calculateWordScore(phraseArray);

        HashMap<String, Double> candidateScores = getCandidateScores(phraseArray, wordScores);
        return Sorting.fastSort(candidateScores);
    }
}
