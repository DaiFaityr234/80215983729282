package spelling;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class MediaPlayer{
	//Main embedded vlc player
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	//Integer store to allow toggling of play/pause button
	public int pp = 0;
	private SpellingAid spellingAid;

	
	public MediaPlayer(int i,SpellingAid spellAid) {
		spellingAid = spellAid;

		//Window surrounding media player
		final JFrame frame = new JFrame("VIDEO REWARD");

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

		final EmbeddedMediaPlayer video = mediaPlayerComponent.getMediaPlayer();

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mediaPlayerComponent, BorderLayout.CENTER); //add video to centre of window

		frame.setContentPane(panel);
		FlowLayout options = new FlowLayout();
		final JPanel tabs = new JPanel(); //set video functionality on top of window
		tabs.setLayout(options);
		options.setAlignment(FlowLayout.TRAILING);

		final JLabel videoTimer = new JLabel("00:00:00"); //set timer on bottom of window
		videoTimer.setSize(60,25);
		panel.add(videoTimer, BorderLayout.SOUTH);

		//Timer is used to display time in video
		final Timer timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int time = (int) (video.getTime()/1000);
				if(time == 0){
					videoTimer.setText("00:00:00");
				} else {

					//Some division is done to display the time in appropriate format
					int second = time%60;
					int min = time/60;
					int minute = min%60;
					int hour = min/60;
					String h = Integer.toString(hour);
					String m = Integer.toString(minute);
					String s = Integer.toString(second);
					if(second < 10){
						s="0"+s;
					}
					if(minute < 10){
						m="0"+m;
					}
					if(hour < 10){
						h="0"+h;
					}
					videoTimer.setText(h+":"+m+":"+s);
				}
			}
		});
		timer.start();

		//Make a mute button that stops playing sound of video
		JButton btnMute = new JButton("MUTE");
		btnMute.setPreferredSize(new Dimension(255, 30));
		tabs.add(btnMute);
		btnMute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.mute();
			}
		});

		//Make a play/pause button that starts/stops playing the video graphics
		final JButton btnPause = new JButton("PAUSE");
		btnPause.setPreferredSize(new Dimension(255, 30));
		tabs.add(btnPause);
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.pause();
				//Toggle text of button to play/pause
				if (pp % 2 == 0){
					btnPause.setText("PLAY");
				}
				else {
					btnPause.setText("PAUSE");
				}
				pp++;

			}

		});

		//Make a rewind button that skips back in the video
		JButton btnRewind = new JButton("REWIND");
		btnRewind.setPreferredSize(new Dimension(255, 30));
		tabs.add(btnRewind);
		btnRewind.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.skip(-2500);
			}
		});

		//Make a fast forward button that skips forward in the video
		JButton btnFastForward = new JButton("FAST FORWARD");
		btnFastForward.setPreferredSize(new Dimension(255, 30));
		tabs.add(btnFastForward);
		btnFastForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.skip(2500);
			}
		});

		//Set dimensions of window
		frame.setLocation(100, 100);
		frame.setSize(1050, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.add(tabs, BorderLayout.NORTH);
		
		//Extra video option
		File f = new File("output.avi");
		if(!f.exists()) { 
			processStarter("ffmpeg -i big_buck_bunny_1_minute.avi -filter_complex '[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]' -map '[v]' -map '[a]' output.avi");
		}
		if (i == 1){
			video.playMedia("big_buck_bunny_1_minute.avi");
		}
		else if (i == 2){
			//Play extra video option
			video.playMedia("output.avi");
		}

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	frame.setVisible(false);
		    	frame.dispose();
		    	spellingAid.nextQuizOptions();
		    }
		});
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
	
	
	
}