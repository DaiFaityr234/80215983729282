package spelling;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;

import spelling.SpellingList.SpellingLevel;
import spelling.VoiceGenerator.Voice;

@SuppressWarnings("serial")
public class SpellingAid extends JFrame implements ActionListener{


	JFrame frame = new JFrame("Spelling_aid"); //Main spelling frame
	final JPanel tabs = new JPanel(); //Main spelling option buttons
	final JPanel controller = new JPanel(); //Main spelling logic functions

	//The Spelling List so that all buttons can access it, will be set in New/Review button
	private SpellingList spellList = null;
	private SpellingLevel spellingLvl = null;

	//The voice generator for Spelling Aid
	public VoiceGenerator voiceGen = null;
	public VoiceGenerator respellGen = null;
	public Voice theVoice = Voice.DEFAULT;
	public double theVoiceSpeed = 1.3;

	//Creating buttons for tab menu
	public JButton newQuiz = new JButton("New Spelling Quiz");
	public JButton reviewMistakes = new JButton("Review Mistakes");
	public JButton viewStats = new JButton("View Statistics");
	public JButton clearStats = new JButton("Clear Statistics");

	//Creating buttons for controller components
	public JLabel spellPrompt = new JLabel("Please spell here:");
	public JTextField userInput = new JTextField();
	public JButton enter = new JButton("Enter");
	public JButton wordListen = new JButton("Listen to the word again");
	public JLabel voxPrompt = new JLabel("Voice Toggle");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JComboBox voxSelect = new JComboBox(new String[]{"Default","Auckland"});
	public JLabel levelIndicator = new JLabel("Level X");

	//Creating main GUI output area
	public JTextArea window = new JTextArea(50,30);
	public JScrollPane scrollBar = new JScrollPane(window);

	//Layout for main GUI
	FlowLayout options = new FlowLayout();

	//To determine whether to clear out welcome text, if true = don't clear
	boolean notFirstTime; 

	//Method to add buttons to main GUI frame
	public void addComponentsToGUI(Container pane) {        


		tabs.setLayout(options);
		options.setAlignment(FlowLayout.TRAILING);
		controller.setLayout(new BoxLayout(controller, BoxLayout.Y_AXIS));

		//Setting sizes of tab buttons
		newQuiz.setPreferredSize(new Dimension(150, 30));
		tabs.add(newQuiz);
		reviewMistakes.setPreferredSize(new Dimension(150, 30));
		tabs.add(reviewMistakes);
		viewStats.setPreferredSize(new Dimension(150, 30));
		tabs.add(viewStats);
		clearStats.setPreferredSize(new Dimension(150, 30));
		tabs.add(clearStats);



		//Setting sizes of spelling components
		spellPrompt.setPreferredSize(new Dimension(150, 30));
		spellPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		controller.add(spellPrompt);
		userInput.setPreferredSize(new Dimension(170, 30));
		userInput.setAlignmentX(Component.CENTER_ALIGNMENT);
		controller.add(userInput);

		enter.setPreferredSize(new Dimension(150, 30));
		enter.setAlignmentX(Component.CENTER_ALIGNMENT);
		controller.add(enter);

		//Spacer to format components on right hand side of GUI
		controller.add(Box.createRigidArea(new Dimension(40,100)));

		//Setting size for "Listen to the word again" button
		wordListen.setFont(new Font("Calibri", Font.BOLD, 10));
		wordListen.setPreferredSize(new Dimension(150, 70));
		wordListen.setAlignmentX(Component.CENTER_ALIGNMENT);
		controller.add(wordListen);

		//Spacer to format components on right hand side of GUI
		controller.add(Box.createRigidArea(new Dimension(40,100)));

		//Setting size for voice selecting combo box
		voxPrompt.setPreferredSize(new Dimension(150, 30));
		voxPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		controller.add(voxPrompt);
		voxSelect.setPreferredSize(new Dimension(150, 30));
		voxSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
		controller.add(voxSelect);

		//Spacer to format components on right hand side of GUI
		controller.add(Box.createRigidArea(new Dimension(40,80)));

		//Setting size for level indicator at the bottom of the GUI
		levelIndicator.setPreferredSize(new Dimension(40, 30));
		levelIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
		controller.add(levelIndicator);

		//Arranging tabs only when GUI is opened for the first time
		pane.add(tabs, BorderLayout.NORTH);
		pane.add(controller, BorderLayout.EAST);

		//Set main text display in centre of GUI
		//Scroll bar allows user to check previous words attempted during current session
		pane.add(scrollBar, BorderLayout.CENTER);
	}
	// Constructor for spelling aid object
	public SpellingAid() {
		notFirstTime = false; 

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(scrollBar);

		// Adding action listeners that perform operations when button is pressed
		newQuiz.addActionListener(this);
		reviewMistakes.addActionListener(this);
		viewStats.addActionListener(this);
		clearStats.addActionListener(this);
		wordListen.addActionListener(this);
		enter.addActionListener(this);
		voxSelect.addActionListener(this);
		addComponentsToGUI(frame.getContentPane());
		frame.setSize(630, 530);
		frame.setVisible(true);
		controller.setVisible(false); //hide controller until spelling quiz starts
		//Display welcome message to GUI
		window.append("                ====================================\n");
		window.append("                               Welcome to the Spelling Aid\n");
		window.append("                ====================================\n");
		window.append("                Please select from one of the options above:");

		//Disable any editing from user
		window.setEditable(false);

		//JTextArea automatically scrolls itself 
		DefaultCaret scroller = (DefaultCaret)window.getCaret();
		scroller.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		//initialise voice generator for the app
		voiceGen = new VoiceGenerator(theVoice,theVoiceSpeed);

		//initialise voice generator for the respell button
		respellGen = new VoiceGenerator(theVoice,theVoiceSpeed);
		respellGen.cancel(true); // immediately cancel it to allow the respell button to work on the first try

		makeSureAllNecessaryFilesArePresent();// check for the presence of the hidden files
	}

	public static void main(String[] args) {
		try {
			// Preferred look and feel
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Make main GUI
				new SpellingAid();
			}
		});
	}
	//Set operations for different buttons
	public void actionPerformed(ActionEvent ae) {
		//Setting internal representation for each option chosen
		if (ae.getSource() == newQuiz) {

			spellList = new SpellingList(); //Create new list of 10 words
			LevelSelector levelSelect = new LevelSelector(); //Create new joptionpane to select level
			if(levelSelect.getLevel()!=0){ // only when a level is selected, that u start changing the window's content

				frame.getContentPane().remove(tabs);
				controller.setVisible(true);
				if(!notFirstTime){
					// clear the window
					window.setText("");
					notFirstTime = true;
				}

				//Display new spelling message to GUI
				window.append("                ====================================\n");
				window.append("                               New Spelling Quiz ( Level "+ levelSelect.getLevel() +")\n");
				window.append("                ====================================\n\n");

				//Start asking questions
				spellList.createLevelList(levelSelect.getLevel(), "new",this);
				levelIndicator.setText("Level "+ spellList.getCurrentLevel());
				spellingLvl=spellList.getQuestion(); // initiate swing worker
				spellingLvl.execute(); // execute quiz
			}
		}
		else if (ae.getSource() == reviewMistakes) {

			spellList = new SpellingList(); //Create new list of 10 words
			LevelSelector levelSelect = new LevelSelector(); //Create new joptionpane to select level
			if(levelSelect.getLevel()!=0){ // only when a level is selected, that u start changing the window's content
				frame.getContentPane().remove(tabs);
				controller.setVisible(true);
				if(!notFirstTime){
					// clear the window
					window.setText("");
					notFirstTime = true;
				}
				//Display new spelling message to GUI
				window.append("                ====================================\n");
				window.append("                             Review Spelling Quiz ( Level "+ levelSelect.getLevel() +")\n");
				window.append("                ====================================\n\n");

				spellList.createLevelList(levelSelect.getLevel(), "review",this);
				levelIndicator.setText("Level "+ spellList.getCurrentLevel());
				spellingLvl=spellList.getQuestion(); // initiate swing worker
				spellingLvl.execute(); // execute quiz
			}
		}
		else if (ae.getSource() == viewStats) {
			// clear the window
			window.setText("");
			//Display new spelling message to GUI
			window.append("                ====================================\n");
			window.append("                                   Spelling Aid Statistics \n");
			window.append("                ====================================\n");

			notFirstTime = false; // to clear the stats

			// instantiate the statistics obj and execute it
			SpellingAidStatistics statsWin = new SpellingAidStatistics(this);
			statsWin.execute();
		}
		else if (ae.getSource() == clearStats) {
			// CLEAR STATS info dialog
			JOptionPane.showMessageDialog(this, ClearStatistics.clearStats(), "Spelling Aid", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (ae.getSource() == enter) {
			// only take in input when it is in the ANSWERING phase
			if(spellList.status.equals("ANSWERING")){
				spellList.setAnswer(clearTxtBox());
				spellList.status="ANSWERED"; // change phase to ASNWERD after accepting answer
			}

		}
		else if (ae.getSource() == wordListen) {
			// this button only works when the voice generator is not generating any voice
			if(!spellList.status.equals("ASKING")&&respellGen.isDone()){
				respellGen = new VoiceGenerator(theVoice,theVoiceSpeed);
				respellGen.setTextForSwingWorker("", spellList.getCurrentWord());
				respellGen.execute();
			}
		}
		else if (ae.getSource() == voxSelect) {
			// sets the chosen voice
			if (voxSelect.getSelectedItem().toString().equals("Default")){
				System.out.println("set to default");
				theVoice = Voice.DEFAULT;

			} else if (voxSelect.getSelectedItem().toString().equals("Auckland")){				
				System.out.println("set to auckland");
				theVoice = Voice.AUCKLAND;
			}
			voiceGen.setVoice(theVoice);
		}

	}

	// get the text from the text box then clears it
	private String clearTxtBox(){
		String theReturn = userInput.getText();
		userInput.setText("");
		return theReturn;
	}

	// checks that all the files that are storing the statistics are present and create any files that do not exist
	private void makeSureAllNecessaryFilesArePresent() {
		File spelling_aid_failed = new File(".spelling_aid_failed");
		File spelling_aid_statistics = new File(".spelling_aid_statistics");
		File spelling_aid_tried_words = new File(".spelling_aid_tried_words");
		try{
			if(! spelling_aid_failed.exists()){
				spelling_aid_failed.createNewFile();
			}
			if(! spelling_aid_statistics.exists()){
				spelling_aid_statistics.createNewFile();
			}
			if(! spelling_aid_tried_words.exists()){
				spelling_aid_tried_words.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void revertToOriginal() {
		frame.getContentPane().add(tabs, BorderLayout.NORTH);
		controller.setVisible(false);
	}

	public void nextQuizOptions() {
		EndOfQuizOptionSelector endSelector = new EndOfQuizOptionSelector(); //Create new joptionpane to select end of quiz options
		if(endSelector.getOption()==0){
			revertToOriginal();
		}
		else if (endSelector.getOption()==1){
			//Display new spelling message to GUI
			window.append("                ====================================\n");
			window.append("                               New Spelling Quiz ( Level "+ spellList.getCurrentLevel() +")\n");
			window.append("                ====================================\n\n");
			
			//Start asking questions
			spellList.createLevelList(spellList.getCurrentLevel(), "new",this);
			levelIndicator.setText("Level "+ spellList.getCurrentLevel());
			spellingLvl=spellList.getQuestion(); // initiate swing worker
			spellingLvl.execute(); // execute quiz
			
		}
		else if (endSelector.getOption()==2){
			int nextLevel = spellList.getCurrentLevel()+1;
			//Display new spelling message to GUI
			window.append("                ====================================\n");
			window.append("                               New Spelling Quiz ( Level "+ nextLevel +")\n");
			window.append("                ====================================\n\n");
			
			//Start asking questions
			spellList.createLevelList(nextLevel, "new",this);
			levelIndicator.setText("Level "+ nextLevel);
			spellingLvl=spellList.getQuestion(); // initiate swing worker
			spellingLvl.execute(); // execute quiz
			
		}
		else if (endSelector.getOption()==3){
			new MediaPlayer(1,this);
			//SwingWorkerMediaPlayer mp = new SwingWorkerMediaPlayer(1,this);
			//mp.execute();
		}
		else if (endSelector.getOption()==4){
			new MediaPlayer(2,this);
			//SwingWorkerMediaPlayer mp = new SwingWorkerMediaPlayer(2,this);
			//mp.execute();

		}

	}

}