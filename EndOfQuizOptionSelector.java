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
	
	//Create option pane to select next action
	public EndOfQuizOptionSelector(){
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
		
		final JButton _replaylevel = new JButton("Replay current level");
		final JButton _nextlevel = new JButton("Play next level");
		final JButton _videoreward = new JButton("Play video reward");
		final JButton _done = new JButton("Done");
		
		//Adding action listeners
		_replaylevel.addActionListener(this);
		_nextlevel.addActionListener(this);
		_videoreward.addActionListener(this);
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
		
		//Add done button
		panel.add(_done);
		
		//Add all panels to main panel
		mainPanel.add(spellingResults);
		mainPanel.add(empty);
		mainPanel.add(panel);
		
		
		return mainPanel;
	}

	public void actionPerformed(ActionEvent e) {
		
		
	}
}
