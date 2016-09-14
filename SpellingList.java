package spelling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

public class SpellingList {

	// THESE ARE TO KEEP TRACK OF QUESTIONS AND ATTEMPT COUNTS
	int questionNo; 			
	int _currentLevel;
	boolean attempt = false; // to record if this was the second attempt
	String wordToSpell ; 	 

	// List to ask questions from 
	ArrayList<String> _currentList ;
	// List to record stats for the current level
	ArrayList<String> _currentFailedList ;
	ArrayList<String> _currentTriedList ;

	// Files for statistics recording
	File wordList;
	File spelling_aid_tried_words;
	File spelling_aid_failed;
	File spelling_aid_statistics;

	// ArrayLists for storing file contents for easier processing later according to levels
	HashMap<Integer, ArrayList<String>> mapOfWords = new HashMap<Integer, ArrayList<String>>();
	HashMap<Integer, ArrayList<String>> mapOfFailedWords = new HashMap<Integer, ArrayList<String>>();
	HashMap<Integer, ArrayList<String>> mapOfTriedWords = new HashMap<Integer, ArrayList<String>>();

	// construction of spellinglist model for current session
	public SpellingList(){
		//INITIALISING LISTS TO STORE VALUES
		// Files for statistics recording
		wordList = new File("NZCER-spelling-lists.txt");
		spelling_aid_tried_words = new File(".spelling_aid_tried_words");
		spelling_aid_failed = new File(".spelling_aid_failed");
		spelling_aid_statistics = new File(".spelling_aid_statistics");

		try {
			// start adding file contents to the appropriate array list

			// WORDLIST
			BufferedReader readWordList = new BufferedReader(new FileReader(wordList));
			String word = readWordList.readLine();
			// array to store words in a level
			ArrayList<String> wordsInALevel = new ArrayList<String>();
			// level at which the word storage is happening
			int newSpellingLevel = 1;
			while(word != null){
				// % = level and so do appropriate things
				if(word.charAt(0) == '%'){
					String[] levelNo = word.split(" ");
					newSpellingLevel = Integer.parseInt(levelNo[1]);
					wordsInALevel = new ArrayList<String>();
					mapOfWords.put(newSpellingLevel,wordsInALevel);
				} else {
					wordsInALevel.add(word);
				}
				word = readWordList.readLine();
			}
			readWordList.close();

			// TRIED WORDS
			BufferedReader readTriedList = new BufferedReader(new FileReader(spelling_aid_tried_words));
			String triedWord = readTriedList.readLine();
			// array to store words in a level
			ArrayList<String> triedWordsInALevel = new ArrayList<String>();
			// level at which the word storage is happening
			int triedLevel = 1;
			while(triedWord != null){
				// % = level and so do appropriate things
				if(triedWord.charAt(0) == '%'){
					String[] levelNo = triedWord.split(" ");
					triedLevel = Integer.parseInt(levelNo[1]);
					triedWordsInALevel = new ArrayList<String>();
					mapOfWords.put(triedLevel,triedWordsInALevel);
				} else {
					triedWordsInALevel.add(triedWord);
				}
				triedWord = readTriedList.readLine();
			}
			readTriedList.close();

			// FAILED WORDS
			BufferedReader readFailList = new BufferedReader(new FileReader(spelling_aid_failed));
			String failedWord = readFailList.readLine();
			// array to store words to review in a level
			ArrayList<String> wordsToReviewInALevel = new ArrayList<String>();
			// level at which the word storage is happening
			int reviewLevel = 1;
			while(failedWord != null){
				// % = level and so do appropriate things
				if(failedWord.charAt(0) == '%'){
					String[] levelNo = failedWord.split(" ");
					reviewLevel = Integer.parseInt(levelNo[1]);
					wordsToReviewInALevel = new ArrayList<String>();
					mapOfWords.put(reviewLevel,wordsToReviewInALevel);
				} else {
					wordsToReviewInALevel.add(failedWord);
				}
				failedWord = readWordList.readLine();
			}
			readFailList.close();

		} catch (IOException e){
			System.out.println("Make sure that the file wordlist which contains all the words that will be quizzed is in the same directory as this runnable jar file");
		}

	}

	// creates a list of words to test according to level and mode
	public void createLevelList(int level, String spellingType){
		questionNo = 0;
		_currentLevel = level;

		// choose list to read from according to mode
		HashMap<Integer, ArrayList<String>> wordMap;
		if(spellingType.equals("new")){
			wordMap = mapOfWords;
		} else {
			wordMap = mapOfFailedWords; 
		}
		
		if(mapOfFailedWords.get(_currentLevel)==null){
			mapOfFailedWords.put(_currentLevel, new ArrayList<String>());
		}
		
		if(mapOfTriedWords.get(_currentLevel)==null){
			mapOfTriedWords.put(_currentLevel, new ArrayList<String>());
		}
		
		ArrayList<String> listOfWordsToChooseFrom = wordMap.get(level);
		ArrayList<String> listOfWordsToTest = new ArrayList<String>();
		Set<String> uniqueWordsToTest = new HashSet<String>();
		if (listOfWordsToChooseFrom.size() < 11){
			listOfWordsToTest = listOfWordsToChooseFrom;
		} else {
			while(uniqueWordsToTest.size() != 10){
				int positionToChoose = (int) Math.floor(Math.random() * listOfWordsToChooseFrom.size());
				uniqueWordsToTest.add(listOfWordsToChooseFrom.get(positionToChoose));
			}
		}
		listOfWordsToTest.addAll(uniqueWordsToTest);

		_currentList = listOfWordsToTest;
		_currentFailedList = mapOfFailedWords.get(_currentLevel);
		_currentTriedList = mapOfTriedWords.get(_currentLevel);

		askNextQuestion();

	}

	// Start asking the new question
	public void askNextQuestion(){
		// ask the next question
		questionNo++;
		// attempt is true when it is the second attempt
		attempt = false;
		wordToSpell = _currentList.get(questionNo);
		processStarter("echo Please spell word " + questionNo + " of " + _currentList.size() + ": " + wordToSpell + " | festival --tts");
	}

	// To check if the answer is right and act accordingly
	public void checkAnswer(String answer){
		// ensure that the answer is valid
		if (!allAlphabet(answer)){
			// replace null with the GUI
			JOptionPane.showMessageDialog(null, "Only ALPHABETS are allowed!!! (No Symbols or Numbers or Spaces)", "Input Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}

		// turn to lower case and then compare
		if(answer.toLowerCase().equals(wordToSpell)){
			processStarter("echo Correct | festival --tts"); // Correct echoed if correct
			if(!attempt){
				record(spelling_aid_statistics,wordToSpell+" Mastered"); // store as mastered
				attempt = true;
			} else {
				record(spelling_aid_statistics,wordToSpell+" Faulted"); // store as faulted
			}
			if(_currentFailedList.contains(wordToSpell)){ // remove from failed list if exists
				_currentFailedList.remove(wordToSpell);
			}
		} else {
			if(!attempt){
				processStarter("echo Incorrect, try once more: "+wordToSpell+" . "+wordToSpell+" . " + "| festival --tts");
				attempt = true;
			} else {
				processStarter("echo Incorrect | festival --tts");
				record(spelling_aid_statistics,wordToSpell+" Failed"); // store as failed
				if(!_currentFailedList.contains(wordToSpell)){ //add to failed list if it doesn't exist
					_currentFailedList.add(wordToSpell);
				}
			}
		}

		if (attempt){
			// store as an attempted word after checking to make sure that there are no duplicates in the tried_words list
			if(!_currentTriedList.contains(wordToSpell)){
				_currentFailedList.add(wordToSpell);
				
			}
		
		}
	}
	
	/// this is to record everything related to the current level to the file
	public void recordStatsFromLevel(){
		Object[] failedKeys = mapOfFailedWords.keySet().toArray();
		Object[] triedKeys = mapOfFailedWords.keySet().toArray();
		
		ClearStatistics.clearFile(spelling_aid_failed);
		ClearStatistics.clearFile(spelling_aid_tried_words);

		
		Arrays.sort(failedKeys);
		Arrays.sort(triedKeys);
		
		for(Object key : failedKeys){
			int dKey = (Integer)key;
			record(spelling_aid_failed,"%Level "+dKey);
			for(String wordToRecord : mapOfFailedWords.get(dKey)){
				record(spelling_aid_failed,wordToRecord);
			}
		}	
		for(Object key : triedKeys){
			int dKey = (Integer)key;
			record(spelling_aid_tried_words,"%Level "+dKey);
			for(String wordToRecord : mapOfTriedWords.get(dKey)){
				record(spelling_aid_tried_words,wordToRecord);
			}
		}	
		
	}


	// to run BASH commands
	private void processStarter(String command){
		// process builder to run bash commands
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
		Process process;
		try {
			process = builder.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// record a word to a file
	public static void record(File file,String word){
		try{
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
			writer.println(word);
			writer.close();
		} catch(IOException e){
			System.out.println("An I/O Error Occurred");
			System.exit(0);
		}
	}

	// function to ensure that the answer the user inputted is valid (in the format that can be accepted)
	private boolean allAlphabet(String answer) {
		char[] chars = answer.toCharArray();
		for (char c : chars) {
			if(!Character.isLetter(c)) {
				return false;
			}
		}
		return true;	
	}

}