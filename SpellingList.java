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
import javax.swing.SwingWorker;

public class SpellingList {

	// THESE ARE TO KEEP TRACK OF QUESTIONS AND ATTEMPT COUNTS
	// Spelling Type
	String spellType;
	// Question Number
	int questionNo; 	
	// Current Level
	int currentLevel;
	// True if question has been attempted (according to current question)
	boolean attempt = false; 
	// Current word to spell
	private String wordToSpell ; 	 
	// The status of the program {ASKING, ANSWERING, ANSWERED} 
	String status;
	// User's answer is stored here
	private String userAnswer = "0";
	// This is the SPELLING AID APP
	private SpellingAid spellingAidApp = null;
	// Number of correct answers
	private int correctAns = 0;

	// List to ask questions from 
	ArrayList<String> currentList ;
	// List to record stats for the current level
	ArrayList<String> currentFailedList ;
	ArrayList<String> currentTriedList ;

	// Files for statistics recording
	File wordList;
	File spelling_aid_tried_words;
	File spelling_aid_failed;
	File spelling_aid_statistics;

	// ArrayLists for storing file contents for easier processing later according to levels
	HashMap<Integer, ArrayList<String>> mapOfWords;
	HashMap<Integer, ArrayList<String>> mapOfFailedWords;
	HashMap<Integer, ArrayList<String>> mapOfTriedWords;

	// construction of spellinglist model for current session
	public SpellingList(){
		//INITIALISING LISTS TO STORE VALUES
		// Files for statistics recording
		wordList = new File("NZCER-spelling-lists.txt");
		spelling_aid_tried_words = new File(".spelling_aid_tried_words");
		spelling_aid_failed = new File(".spelling_aid_failed");
		spelling_aid_statistics = new File(".spelling_aid_statistics");

		mapOfWords = new HashMap<Integer, ArrayList<String>>();
		mapOfFailedWords = new HashMap<Integer, ArrayList<String>>();
		mapOfTriedWords = new HashMap<Integer, ArrayList<String>>();

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
					mapOfTriedWords.put(triedLevel,triedWordsInALevel);
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
					mapOfFailedWords.put(reviewLevel,wordsToReviewInALevel);
				} else {
					wordsToReviewInALevel.add(failedWord);
				}
				failedWord = readFailList.readLine();
			}
			readFailList.close();

		} catch (IOException e){
			e.printStackTrace();
		}

	}

	// Creates a list of words to test according to level and mode
	public void createLevelList(int level, String spellingType, SpellingAid spellAidApp){
		// For every level these following variables start as follows
		questionNo = 0;
		correctAns = 0;
		currentLevel = level;
		spellType=spellingType;
		spellingAidApp = spellAidApp;
		status = "ASKING";

		// choose list to read from according to mode
		HashMap<Integer, ArrayList<String>> wordMap;
		if(spellingType.equals("new")){
			wordMap = mapOfWords;
		} else {
			wordMap = mapOfFailedWords; 
		}

		// if level has not been attempted, create a list for that level since it won't exist
		if(mapOfFailedWords.get(currentLevel)==null){
			mapOfFailedWords.put(currentLevel, new ArrayList<String>());
		}
		if(mapOfTriedWords.get(currentLevel)==null){
			mapOfTriedWords.put(currentLevel, new ArrayList<String>());
		}

		// produce 10 random words from the correct list of words
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

		// initialise lists for checking
		currentList = listOfWordsToTest;
		currentFailedList = mapOfFailedWords.get(currentLevel);
		currentTriedList = mapOfTriedWords.get(currentLevel);
	}

	// This class will carry out a spelling level's question asking and answer checking
	class SpellingLevel extends SwingWorker<Void, Void>{
		int noOfQ = getNoOfQuestions();
		protected Void doInBackground(){
			if(noOfQ!=0){
				while(true){
					// ASKING = time to ask the next question
					if(status.equals("ASKING")){
						// this while loop will keep looping until all questions have been asked
						if(questionNo == getNoOfQuestions()){
							break;
						} else {
							askNextQuestion();
						}
					}
					// while the question is UNANSWERED stay in this while loop and don't check for answer, status = "ANSWERING"
					while(!status.equals("ANSWERED")){
						@SuppressWarnings("unused")
						int hi = 99999999*9999999; // to keep the while loop busy
					}
					// check answer when question is ANSWERED
					checkAnswer();
				}
			} else {
				spellingAidApp.window.append(" There are no words to review in this level.\n\n");

			}
			return null;
		}

		// when done
		protected void done(){
			spellingAidApp.window.append("\n You have gotten "+ correctAns +" out of "+ noOfQ + " correct !\n\n" );
			recordStatsFromLevel();
			if(spellType.equals("new")){
				spellingAidApp.nextQuizOptions();
			} else {
				spellingAidApp.revertToOriginal();
			}
		}

	}

	// this is to get a SwingWorker object that will run the spelling level's question asking and answer checking in the background
	public SpellingLevel getQuestion(){
		return new SpellingLevel();
	}

	// Start asking the new question
	private void askNextQuestion(){
		// attempt is true when it is the second attempt
		attempt = false;
		// starts at 0
		wordToSpell = currentList.get(questionNo);
		// then increment the question no to represent the real question number
		questionNo++;

		spellingAidApp.window.append("\n Spell word " + questionNo + " of " + currentList.size() + ": ");
		spellingAidApp.voiceGen.sayText("Please spell word " + questionNo + " of " + currentList.size() + ": ",wordToSpell);
		//processStarter("echo Please spell word " + questionNo + " of " + currentList.size() + ": " + wordToSpell + " | festival --tts");

		// after ASKING, it is time for ANSWERING
		status = "ANSWERING";
	}

	// To check if the answer is right and act accordingly
	private void checkAnswer(){

		// ensure that the answer is valid
		if (!validInput(userAnswer)){
			// warning dialog for invalid user input
			JOptionPane.showMessageDialog(spellingAidApp, "Only ALPHABETS and \" \' \" are allowed!!!", "Input Warning",JOptionPane.WARNING_MESSAGE);
			// go back to ANSWERING since current answer is invalid
			status = "ANSWERING";
			return;
		} 


		// if it is valid, start the checking
		spellingAidApp.window.append(userAnswer+"\n");
		// turn to lower case for BOTH and then compare
		if(userAnswer.toLowerCase().equals(wordToSpell.toLowerCase())){
			// Correct echoed if correct
			spellingAidApp.voiceGen.sayText("Correct","");
			//processStarter("echo Correct | festival --tts"); 
			if(!attempt){
				record(spelling_aid_statistics,wordToSpell+" Mastered"); // store as mastered
			} else {
				record(spelling_aid_statistics,wordToSpell+" Faulted"); // store as faulted
			}
			if(currentFailedList.contains(wordToSpell)){ // remove from failed list if exists
				currentFailedList.remove(wordToSpell);
			}
			correctAns++; //question answered correctly
			attempt = true; // question has been attempted
			// answer is correct and so proceed to ASKING the next question
			status = "ASKING";
		} else {
			if(!attempt){
				spellingAidApp.window.append("      Incorrect, try once more: ");
				spellingAidApp.voiceGen.sayText("Incorrect, try once more: ",wordToSpell+" . "+wordToSpell+" . ");
				//processStarter("echo Incorrect, try once more: "+wordToSpell+" . "+wordToSpell+" . " + "| festival --tts");
				// answer is wrong and a second chance is given and so back to ANSWERING
				status = "ANSWERING";
			} else {
				spellingAidApp.voiceGen.sayText("Incorrect.","");
				//processStarter("echo Incorrect | festival --tts");
				record(spelling_aid_statistics,wordToSpell+" Failed"); // store as failed
				if(!currentFailedList.contains(wordToSpell)){ //add to failed list if it doesn't exist
					currentFailedList.add(wordToSpell);
				}
				// answer is wrong on second attempt and so back to ASKING
				status = "ASKING";
			}
			attempt = true; // question has been attempted
		}

		if (attempt){
			// store as an attempted word after checking to make sure that there are no duplicates in the tried_words list
			if(!currentTriedList.contains(wordToSpell)){
				currentTriedList.add(wordToSpell);

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

	/*
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
	 */

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
	private boolean validInput(String answer) {
		char[] chars = answer.toCharArray();
		// blank = unacceptable
		if(answer.equals("")){
			return false;
		}
		// first letter symbol = unacceptable
		if(!Character.isLetter(chars[0])){
			return false;
		}
		// accept any space or ' after first letter
		for (char c : chars) {
			if(!Character.isLetter(c) && (c != '\'') && (c != ' ')) {
				return false;
			}
		}
		return true;	
	}

	// for the GUI to set the answer
	public void setAnswer(String theUserAnswer){
		userAnswer=theUserAnswer;
	}

	// get number of questions
	public int getNoOfQuestions(){
		return currentList.size();
	}

	// get number of questions
	public int getCurrentLevel(){
		return currentLevel;
	}

	public String getCurrentWord(){
		return wordToSpell;
	}
}