package spelling;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Class that instantiates a JPanel that displays the statistics of attempted words
 * @author yyap601
 *
 */
public class ViewStatistics extends JPanel{
	
	// TITLE OF THE PANEL
	private JLabel title = new JLabel("Spelling Aid Statistics");
	// THE TEXT AREA TO DISPLAY THE STATISTICS
	private JTextArea statistics = new JTextArea(20,10);
	// TO HAVE A SCROLLBAR FOR THE TEXT AREA	
	private JScrollPane statisticsWithScrollBar;

	public ViewStatistics(){
		
		// set font of title
		title.setFont(new Font("TimesRoman", Font.BOLD, 20));
		// set the layout of the container to grid bag layout
		this.setLayout(new GridBagLayout());

		// add scroll bar to statistics area if needed (i.e. if there are too many things to display)
		statisticsWithScrollBar = new JScrollPane(statistics, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// add the components to the panel that will be containing all of them
		//SpellingAid.addSpellingAidComponent(this, title, 0, 0, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
		//SpellingAid.addSpellingAidComponent(this, statisticsWithScrollBar, 0, 1, 2, 4, 1, 4, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		
		// make the display area uneditable 
		statistics.setEditable(false);
		// start the function to get the stats
		getStatistics();

	}

	// function to start appending statistics to the JTextArea
	private void getStatistics(){

		int zeroWords = 0; // counter to check if there are no words
		// Files for statistics checking
		File triedList = new File(".spelling_aid_tried_words");
		File statsList = new File(".spelling_aid_statistics");
		
		// ArrayLists for storing file contents for easier processing later on
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> wordStats = new ArrayList<String>();

		try {
			// start reading the file contents
			BufferedReader getWord = new BufferedReader(new FileReader(triedList));
			// start adding file contents to the appropriate array list
			String word = getWord.readLine();
			// TRIED LIST
			while(word != null){
				zeroWords++; // increment counter , if never incremented, there are no words
				words.add(word);
				word = getWord.readLine();
			}
			getWord.close();
			// STATS LIST
			BufferedReader getStats = new BufferedReader(new FileReader(statsList));
			word = getStats.readLine();
			while(word != null){
				wordStats.add(word);
				word = getStats.readLine();
			}
			getStats.close();
		} 
		catch(IOException e){
			System.out.println("An I/O Error Occurred");
			System.exit(0);
		}
		
		// sort the tried words array list so that words are displayed in alphabetical order
		Collections.sort(words);
		
		if (zeroWords == 0){
			// if there are no words attempted, then append the following to be displayed
			statistics.append("**NO ATTEMPTED WORDS**");
		} else {
			// start checking for eery tried words' respective mastered/faulted/failed rates and display results
			for(String contents : words){
				int master = 0;
				int fault = 0;
				int fail = 0;
				for (String stat : wordStats){
					String[] wordNStat = stat.split(" ");
					if(wordNStat[0].equals(contents)){
						if(wordNStat[1].equals("Mastered")){
							master++;
						} else if(wordNStat[1].equals("Faulted")){
							fault++;
						} else {
							fail++;
						}
					}
				}
				statistics.append(contents + " :\n");
				statistics.append("    Mastered " + master + " \n");
				statistics.append("    Faulted " + fault + " \n");
				statistics.append("    Failed " + fail + " \n\n");

			}
		}

	}



}
