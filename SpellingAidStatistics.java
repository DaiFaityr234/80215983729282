package spelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingWorker;

public class SpellingAidStatistics extends SwingWorker<Void,String>{
	
	// This is the SPELLING AID APP
	SpellingAid spellAidApp;
	
	// Files for statistics viewing
	File spelling_aid_tried_words;
	File spelling_aid_statistics;
	
	// ArrayLists for storing file contents for easier processing later according to levels
	HashMap<Integer, ArrayList<String>> mapOfTriedWords;	
	ArrayList<String> wordStats;
	
	int zeroWords; // counter to check if there are no words


	public SpellingAidStatistics(SpellingAid spellingAidApp){
		
		// initialise variables appropriately
		spellAidApp=spellingAidApp;
		zeroWords = 0; 
		spelling_aid_tried_words = new File(".spelling_aid_tried_words");
		spelling_aid_statistics = new File(".spelling_aid_statistics");
		wordStats = new ArrayList<String>();
		mapOfTriedWords = new HashMap<Integer, ArrayList<String>>();
		
		// store variables in data structures
		try {
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
					zeroWords++; // if it is greater than 0 = there are statistics available for viewing
				}
				triedWord = readTriedList.readLine();
			}
			readTriedList.close();

			// STATS LIST
			BufferedReader getStats = new BufferedReader(new FileReader(spelling_aid_statistics));
			String word = getStats.readLine();
			while(word != null){
				wordStats.add(word);
				word = getStats.readLine();
			}
			getStats.close();
		} 
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	// SWINGWORKER ~~
	protected Void doInBackground(){
		if(zeroWords == 0){
			publish("\n There Are NO Attempted Words !!!");
		} else { // tried words file not empty
			// go through all the attempted levels
			for(int i : mapOfTriedWords.keySet()){
				// get a list of attempted words according to the level
				ArrayList<String> triedWordsList = mapOfTriedWords.get(i);
				// ERROR handling: if there is a level but 0 content, just skip it
				if(triedWordsList.size()==0){
					continue;
				}
				// TITLE
				publish("\n Level "+i+" Statistics:\n\n");
				// SORT them
				Collections.sort(triedWordsList);
				// Check for statistic of words by getting matches and then display the results
				for(String wd : triedWordsList){
					int master = 0;
					int fault = 0;
					int fail = 0;
					for(String stat : wordStats){
						String[] wordNStat = stat.split(" ");
						if(wordNStat[0].equals(wd)){
							if(wordNStat[1].equals("Mastered")){
								master++;
							} else if(wordNStat[1].equals("Faulted")){
								fault++;
							} else {
								fail++;
							}
						}
					}
					publish(" "+wd + " :\n");
					publish("     Mastered " + master + " ");
					publish("     Faulted " + fault + " ");
					publish("     Failed " + fail + " \n");
				}
			}	
		}
		return null;
	}
	
	// this class displays data by publishing them to the JTextArea
	protected void process(List<String> statsData) {
		for (String data : statsData) {
			spellAidApp.window.append(data);;
		}
	}


}