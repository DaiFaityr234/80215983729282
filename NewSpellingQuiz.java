package spelling;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * NewSpellingQuiz will create an instance based on the type of Spelling Quiz (NEW/REVIEW)
 * @author yyap601
 *
 */

@SuppressWarnings("serial")
public class NewSpellingQuiz extends JPanel implements ActionListener{
	// QUESTIONS WILL BE ASKED USING JOPTIONPANE
	
	// THERE WILL BE A TOP AND BOTTOM PANEL IN THE JOPTIONPANE
	private JPanel top = new JPanel();
	private JPanel bottom = new JPanel();

	private JLabel query = new JLabel("New Spelling Quiz"); // THE QUERY BESIDE THE ANSWER BOX
	private JTextField answerField = new JTextField(20); // ANSWERS WILL BE ENTERED HERE
	private JButton respellBtn = new JButton("Press this to Listen Again"); // RESPELL BUTTON
	private String[] options = new String[]{"        YES        "};  // THE OPTION BUTTON {FOR USER TO CONFIRM ANSWER}
	
	private String wordToSpell; // the word to spell
	private String answer = "0"; // the answer
	private String type; // store the type of spelling quiz

	public NewSpellingQuiz(){
		// set layout of the NewSpellingQuiz Panel that will be inserted into JOptionPane for asking questions
		this.setLayout(new GridLayout(0,1,6,6));
		
		// add listeners
		respellBtn.addActionListener(this);
		// set font of query
		query.setFont(new Font("TimesRoman", Font.BOLD, 14));
		
		// add components to panels
		top.add(query);
		top.add(answerField);
		bottom.add(respellBtn);

		// add top and bottom panels to the main panel (NewSpellingAid)
		this.add(top);
		this.add(bottom);
		
		// this will only be available if the user spells the word wrongly again in REVIEW mode
		respellBtn.setEnabled(false);
		// help users understand why the button was disabled
		respellBtn.setToolTipText("Only available if you are wrong again");

	}

	public NewSpellingQuiz(String quizType){
		this();
		if(quizType.equals("NEW")){
			this.remove(bottom);
		}
		type = quizType;
	}

	public boolean startSpellingQuiz(SpellingAid spellAid) {
		int noOfQuestions; // number of questions that's going to be asked
		int wordListLineCount = 0; // number of words in the word list
		int failedWordsListLineCount = 0; // number of words in the failed list
		
		// Files for statistics recording
		File wordList = new File("wordlist");
		File spelling_aid_tried_words = new File(".spelling_aid_tried_words");
		File spelling_aid_failed = new File(".spelling_aid_failed");
		File spelling_aid_statistics = new File(".spelling_aid_statistics");
		
		// ArrayLists for storing file contents for easier processing later on
		ArrayList<String> wordsToAsk;
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> triedWords = new ArrayList<String>();
		ArrayList<String> failedWords = new ArrayList<String>();

		try {
			// start adding file contents to the appropriate array list
			// WORDLIST
			BufferedReader readWordList = new BufferedReader(new FileReader(wordList));
			String word = readWordList.readLine();
			while(word != null){
				words.add(word);
				wordListLineCount++;
				word = readWordList.readLine();
			}
			readWordList.close();
			// TRIED WORDS
			BufferedReader readTriedList = new BufferedReader(new FileReader(spelling_aid_tried_words));
			String triedWord = readTriedList.readLine();
			while(triedWord != null){
				triedWords.add(triedWord);
				triedWord = readTriedList.readLine();
			}
			readTriedList.close();
			// FAILED WORDS
			BufferedReader readFailList = new BufferedReader(new FileReader(spelling_aid_failed));
			String failedWord = readFailList.readLine();
			while(failedWord != null){
				failedWords.add(failedWord);
				failedWordsListLineCount++;
				failedWord = readFailList.readLine();
			}
			readFailList.close();
		} catch (IOException e){
			System.out.println("Make sure that the file wordlist which contains all the words that will be quizzed is in the same directory as this runnable jar file");
		}


		// assign number of questions based on the number of lines in the file specific to the type of quiz
		if(type.equals("NEW")){
			noOfQuestions = wordListLineCount;
			wordsToAsk = words;
		} else {
			noOfQuestions = failedWordsListLineCount;
			wordsToAsk = failedWords;
		}
		
		// if there are no questions return true immediately to signify it
		if(noOfQuestions == 0){
			return true;
		}
		
		// if there are more than 3 possible questions, make it 3
		if(noOfQuestions>3){
			noOfQuestions = 3;
		}
		
		// get 3 random words from the words to ask list
		String word1 = wordsToAsk.get((int)Math.ceil(Math.random() * (wordsToAsk.size()-1)));
		String word2 = wordsToAsk.get((int)Math.ceil(Math.random() * (wordsToAsk.size()-1)));
		String word3 = wordsToAsk.get((int)Math.ceil(Math.random() * (wordsToAsk.size()-1)));

		// count the number of questions asked
		int questionCount = 0;
		while (questionCount != noOfQuestions) {
			questionCount++;
			
			// make sure that all three words asked are all unique
			if (questionCount == 1){
				wordToSpell = word1;
			} else if (questionCount == 2){
				while (word2.equals(word1)){
					word2 = wordsToAsk.get((int)Math.ceil(Math.random() * (wordsToAsk.size()-1)));
				}
				wordToSpell = word2;
			} else if (questionCount == 3){
				while ( word3.equals(word1) || word3.equals(word2)){
					word3 = wordsToAsk.get((int)Math.ceil(Math.random() * (wordsToAsk.size()-1)));
				}
				wordToSpell = word3;
			}
			
//---------------------------------------------------------------------------------------------------------------
			//prompts user for answer
			query.setText( "Spell word " + questionCount + " of " + noOfQuestions + ": ");
			processStarter("echo Please spell word " + questionCount + " of " + noOfQuestions + ": " + wordToSpell + " | festival --tts");
			JOptionPane.showOptionDialog(spellAid, this, "Spelling Quiz: Question " + questionCount + " of " + noOfQuestions , JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options , null);
			
			// get the answer and then clear the text field
			answer = clearTxtBox();
			// keep asking until a valid input is given
			while (!allAlphabet(answer)){
				JOptionPane.showMessageDialog(this, "Only ALPHABETS are allowed!!! (No Symbols or Numbers or Spaces)", "Input Warning",JOptionPane.WARNING_MESSAGE);
				JOptionPane.showOptionDialog(spellAid, this, "Spelling Quiz: Question " + questionCount + " of " + noOfQuestions, JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options , null);
				answer = clearTxtBox();
			}
			// turn to lower case and then compare
			if(answer.toLowerCase().equals(wordToSpell)){
				processStarter("echo Correct | festival --tts"); // Correct echoed if correct
				answer = clearTxtBox();
				record(spelling_aid_statistics,wordToSpell+" Mastered"); // store as mastered
				if(failedWords.contains(wordToSpell)){ // remove from failed list if exists
					failedWords.remove(wordToSpell);
				}
			} else {
				processStarter("echo Incorrect, try once more: "+wordToSpell+" . "+wordToSpell+" . " + "| festival --tts");
				// if mistake occurs again, enable respell button
				respellBtn.setEnabled(true);
				JOptionPane.showOptionDialog(spellAid, this, "Spelling Quiz: Respell question " + questionCount + " of " + noOfQuestions, JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options , null);
				answer = clearTxtBox();
				while (!allAlphabet(answer)){
					JOptionPane.showMessageDialog(null, "Only ALPHABETS are allowed!!! (No Symbols or Numbers or Spaces)", "Input Warning",JOptionPane.WARNING_MESSAGE);
					JOptionPane.showOptionDialog(spellAid, this, "Spelling Quiz: Question " + questionCount + " of " + noOfQuestions, JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options , null);
					answer = clearTxtBox();
				}
				respellBtn.setEnabled(false); // disable respell button for the next question
				
				// turn to lower case and compare
				if(answer.toLowerCase().equals(wordToSpell)){
					processStarter("echo Correct | festival --tts");
					record(spelling_aid_statistics,wordToSpell+" Faulted"); // store as faulted
					if(failedWords.contains(wordToSpell)){ // remove from failed list if it exists
						failedWords.remove(wordToSpell);
					}
				} else {
					processStarter("echo Incorrect | festival --tts");
					record(spelling_aid_statistics,wordToSpell+" Failed"); // store as failed
					if(!failedWords.contains(wordToSpell)){ //add to failed list if it doesn't exist
						failedWords.add(wordToSpell);
					}
				}
			}


			// store as an attempted word after checking to make sure that there are no duplicates in the tried_words list
			if(!triedWords.contains(wordToSpell)){
				record(spelling_aid_tried_words,wordToSpell); 
			}
			
			// clear failed list and add all the correct failed words into the list 
			ClearStatistics.clearFile(spelling_aid_failed);
			for(String failed : failedWords){
				record(spelling_aid_failed,failed);
			}

		}
		
		// not no words available for quizzing
		return false;
	}



	// for when the respell button is pressed
	public void actionPerformed(ActionEvent e) {
		// spell out the word with festival
		processStarter("echo " + wordToSpell +" | festival --tts");
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
	
	// get the text from the text box then clears it
	private String clearTxtBox(){
		String theReturn = answerField.getText();
		answerField.setText("");
		return theReturn;
	}
	
	// record a word to a file
	private static void record(File file,String word){
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
