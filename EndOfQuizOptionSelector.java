package spelling;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class EndOfQuizOptionSelector implements ActionListener{
	JButton _replaylevel;
	JButton _nextlevel;
	JButton _videoreward ;
	JButton _videoreward2;
	JButton _done;
	int option;
	//SpellingAid sA;
	//Create option pane to select next action
	public EndOfQuizOptionSelector(){
		//sA=sa;
		JOptionPane.showOptionDialog(null,
				makePanel(),
				"Spelling Quiz Options",
				JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{} , null);
	}
	
	//Method to make panel for options
	private JPanel makePanel() {
		
		JPanel mainPanel = new JPanel(); //main frame for option panel
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); //stack components vertically
		
		//Label to print out results of spelling quiz
		JLabel spellingResults = new JLabel("Spelling Quiz Results: X/10 correct");
		spellingResults.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Label to add spacing to option panel for aesthetic purposes
		JLabel empty = new JLabel("                                                    ");
		
		//Main panel that contains buttons
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));//stack buttons horizontally in panel
		
		_replaylevel = new JButton("Replay current level");
		_nextlevel = new JButton("Play next level");
		_videoreward = new JButton("Play video reward 1");
		_videoreward2 = new JButton("Play video reward 2");
		_done = new JButton("Done");
		
		//Adding action listeners
		_replaylevel.addActionListener(this);
		_nextlevel.addActionListener(this);
		_videoreward.addActionListener(this);
		_videoreward2.addActionListener(this);
		_done.addActionListener(this);
		
		//Add replay level button
		panel.add(_replaylevel);
		
		//Add spacer between buttons for aesthetic purposes
		panel.add(Box.createRigidArea(new Dimension(15,20)));
		
		//Add next level button
		panel.add(_nextlevel);
		
		//Add spacer between buttons for aesthetic purposes
		panel.add(Box.createRigidArea(new Dimension(15,20)));
		
		//Add video reward button
		panel.add(_videoreward);
		
		//Add spacer between buttons for aesthetic purposes
		panel.add(Box.createRigidArea(new Dimension(15,20)));
		
		//Add video reward 2 button
		panel.add(_videoreward2);
		
		//Add spacer between buttons for aesthetic purposes
		panel.add(Box.createRigidArea(new Dimension(15,20)));
		
		//Add done button
		panel.add(_done);
		
		//Add all panels to main panel
		mainPanel.add(spellingResults);
		mainPanel.add(empty);
		mainPanel.add(panel);
		
		
		return mainPanel;
	}

	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == _replaylevel){
			option = 1;
			JOptionPane.getRootFrame().dispose(); 
		} else if(ae.getSource() == _nextlevel){
			option = 2;
			JOptionPane.getRootFrame().dispose(); 
		} else if(ae.getSource() == _videoreward){
			option = 3;
			JOptionPane.getRootFrame().dispose(); 

			//new MediaPlayer(1,sA);
		} else if(ae.getSource() == _videoreward2){
			option = 4;
			JOptionPane.getRootFrame().dispose(); 

			//new MediaPlayer(2,sA);
		} else if(ae.getSource() == _done){
			option = 0;
			JOptionPane.getRootFrame().dispose(); 
		}
		
	}
	public int getOption() {
		return option;
	}
	
}