package spelling;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class SpellingAid extends JFrame implements ActionListener{

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
	public JComboBox voxSelect = new JComboBox(new String[]{"Voice 1","Voice 2","Voice 3"});
	public JLabel levelIndicator = new JLabel("Level X");

	//Creating main GUI output area
	public JTextArea window = new JTextArea(50,30);
	
	public JScrollPane scrollBar = new JScrollPane(window);
	//Layout for main GUI
	FlowLayout options = new FlowLayout();

	//Internal store of the option user requested 
	public int option = 0;

	//Method to add buttons to main GUI frame
	public void addComponentsToGUI(Container pane) {        

		final JPanel tabs = new JPanel();
		final JPanel controller = new JPanel();
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

		//Arranging tabs and controller
		pane.add(tabs, BorderLayout.NORTH);
		pane.add(controller, BorderLayout.EAST);
		
		//Set main text display in centre of GUI
		//Scroll bar allows user to check previous words attempted during current session
		pane.add(scrollBar, BorderLayout.CENTER);
	}
	// Constructor for spelling aid object
	public SpellingAid() {

		JFrame frame = new JFrame("Spelling_aid");
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
		frame.setSize(630, 500);
		frame.setVisible(true);
		
		//Display welcome message to GUI
		window.append("                ====================================\n");
		window.append("                               Welcome to the Spelling Aid\n");
		window.append("                ====================================\n");
		window.append("                Please select from one of the options above:\n");
		
		//Disable any editing from user
		window.setEditable(false);
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
			SpellingList newList = new SpellingList(); //Create new list of 10 words
			LevelSelector levelSelect = new LevelSelector(newList,"new"); //Create new joptionpane to select level
			
			option = 1;
		}
		if (ae.getSource() == reviewMistakes) {
			SpellingList newList = new SpellingList(); //Create new list of 10 words
			LevelSelector levelSelect = new LevelSelector(newList,"review"); //Create new joptionpane to select level
			option = 2;
		}
		if (ae.getSource() == viewStats) {

			option = 3;
		}
		if (ae.getSource() == clearStats) {
			try {
				wordToSpeech("four four four");
			} catch (IOException e) {
				e.printStackTrace();
			}
			option = 4;
		}
		if (ae.getSource() == enter) {

			option = 5;
		}
		if (ae.getSource() == wordListen) {

			option = 6;
		}
		if (ae.getSource() == voxSelect) {
			if (voxSelect.getSelectedItem().toString()=="Voice 1"){
				
			} else if (voxSelect.getSelectedItem().toString()=="Voice 2"){
				
			} else if (voxSelect.getSelectedItem().toString()=="Voice 3"){
			}
			option = 7;
		}
	}
	public void wordToSpeech(String word) throws IOException{
		String command = "echo "+word+" | festival --tts";
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
		@SuppressWarnings("unused")
		Process process = pb.start();
	}
}