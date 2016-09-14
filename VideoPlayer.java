package spelling;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class VideoPlayer extends JPanel {
	private static VideoPlayer comp;

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	private JPanel videoPanel;
	private JSlider videoSlider;
	private Timer videoSliderClock;
	private JLabel videoTimer;

	private JButton play;
	private JButton pause;
	private JButton stop;

	private JButton back;
	private JButton forward;

	private int speed;
	private Timer timer;

	private JButton mute;
	private JButton sound;
	private JSlider volume;


	public static VideoPlayer getInstance() {
		if (comp == null) {
			comp = new VideoPlayer();
		}
		return comp;
	}

	private VideoPlayer() {

		//Allows the panel to change size when absolute positioning is used
		addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			public void ancestorResized(HierarchyEvent e) {
				resize();
			}
		});
		this.setVisible(true);
		setSize(900, 500);
		setLayout(null);

		//Methods that are added to the video player functionality
		addPlayer();
		addvideoSlider();
		addTimeLabel();

		playButton();
		pauseButton();
		stopButton();

		backButton();
		forwardButton();

		muteButton();


		//sets the speed to be zero
		speed = 0;
		//Timer to start when fast forward or back buttons are pressed
		//Adds fast-forward and rewind to video player
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(video.getTime() == 0 || video.getTime() == video.getLength()){
					speed = 0;
					timer.stop();
				}
				//Speed of fast forward/rewind changes based on
				//number of times button is pressed
				switch (speed){
				case -3:
					video.skip(-4500);
					break;
				case -2:
					video.skip(-1500);
					break;
				case -1:
					video.skip(-500);
					break;
				case 1:
					video.skip(500);
					break;
				case 2:
					video.skip(1500);
					break;
				case 3:
					video.skip(4500);
					break;
				default:
				}

				if(speed == 0){
					pause.setVisible(true);
					play.setVisible(false);
				}
			}
		});
		//This clock displays the time in the video and updates the scrollBar
		videoSliderClock = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int time = (int) (video.getTime()/1000);
				videoSlider.setValue(time);
				videoSlider.setMaximum((int)video.getLength()/1000);

				if(video.getTime() == video.getLength()) {
					speed = 0;
					timer.stop();
					video.stop();
					pause.setVisible(false);
					play.setVisible(true);
					toggleStopButtons(false);
				}
			}
		});
	}

	//This method adds the video player
	private void addPlayer() {
		videoPanel = new JPanel(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();

		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		videoPanel.setLocation(0,0);
		videoPanel.setVisible(true);

		add(videoPanel);
	}

	//This method adds the video slider 
	private void addvideoSlider(){
		//Timer to change the video slider
		videoSlider = new JSlider(JSlider.HORIZONTAL);
		//Allows user to drag the slider to change the time of the video.
		videoSlider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				videoSliderClock.stop();
				int time = videoSlider.getValue();
				video.setTime(time*1000);
				videoSliderClock.start();
			}
		});

		videoSlider.setValue(0);
		add(videoSlider);
	}

	//This method adds the time label
	private void addTimeLabel(){
		videoTimer = new JLabel("00:00:00");
		videoTimer.setSize(60,25);
		add(videoTimer);

		//Timer is used to display time in video
		Timer ticker = new Timer(500, new ActionListener() {
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
		ticker.start();
	}

	//This method adds the play button
	private void playButton() {
		play = new JButton();
		setIcon(play,"/YJYHCCProtoSpell/icons/play.png");

		//plays the video depending on situation 
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				//Checks if the video was fast forwarded. 
				if (speed != 0) {
					speed = 0;
					pause.setVisible(true);
					play.setVisible(false);
					//Checks if the video video has been stopped
				} else if (!(stop.isEnabled())) {
					video.playMedia(VideoFrame.getInstance().original.getAbsolutePath());
					videoSliderClock.start(); 
					pause.setVisible(true);
					play.setVisible(false);
					toggleStopButtons(true);
					//Checks if the video has been paused.
				} else if (!(video.isPlaying())) {
					video.pause();
					pause.setVisible(true);
					play.setVisible(false);
				}
			}
		});

		play.setEnabled(false);
		play.setSize(32,32);
		add(play);
	}

	//This method adds the pause button
	private void pauseButton(){
		pause = new JButton();
		setIcon(pause,"/YJYHCCProtoSpell/icons/pause.png");

		//pauses the video when button pressed
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.pause();
				pause.setVisible(false);
				play.setVisible(true);
			}
		});

		pause.setVisible(false);
		pause.setSize(32,32);
		add(pause);
	}

	//This method adds the stop button
	private void stopButton(){
		stop = new JButton();
		setIcon(stop,"/YJYHCCProtoSpell/icons/stop.png");

		//Stops the video when button pressed, also toggles buttons
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 0;
				timer.stop();
				video.stop();
				pause.setVisible(false);
				play.setVisible(true);
				toggleStopButtons(false);
			}
		});

		stop.setEnabled(false);
		stop.setSize(32,32);
		add(stop);
	}

	//This method adds the rewind button
	private void backButton(){
		back = new JButton();	
		setIcon(back,"/YJYHCCProtoSpell/icons/back.png");

		//Rewinds the video when the button is pressed
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(speed>-3){
					speed--; //reduce the speed
				}
				play.setVisible(true);
				pause.setVisible(false);
				timer.start();
			}
		});

		back.setEnabled(false);
		back.setSize(32,32);;
		add(back);
	}

	//This method adds the fast forward button
	private void forwardButton(){
		forward = new JButton();	
		setIcon(forward,"/YJYHCCProtoSpell/icons/forward.png");

		//Fast forwards the video
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(speed<3){
					speed++; //Increases the speed
				}
				play.setVisible(true);
				pause.setVisible(false);
				timer.start();
			}
		});

		forward.setEnabled(false);
		forward.setSize(32,32);
		add(forward);
	}

	//This method adds the mute button
	private void muteButton(){
		mute = new JButton();
		setIcon(mute,"/YJYHCCProtoSpell/icons/mute.png");

		mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//unmutes the video if it is muted
				if(video.isMute()){
					//If the video was "muted" via the volume slider, then change the icon
					if(video.getVolume() == 0){
						video.setVolume(100);
						setIcon(sound,"/se206_a03/icons/highsound.png");
					}
					video.mute(false);
					setIcon(mute,"/se206_a03/icons/mute.png");
					//Mutes the video
				} else {
					video.mute(true);
					setIcon(mute,"/se206_a03/icons/lowsound.png");
				}
			}
		});

		mute.setEnabled(false);
		mute.setSize(32,32);
		add(mute);
	}

	private void setIcon(JButton button,String location){
		try {
			Image img = ImageIO.read(getClass().getResource(location));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
	}

	//This method toggles the buttons when the stopped button is pressed.
	private void toggleStopButtons(boolean b){
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
		sound.setEnabled(b);
	}

	//This method allows resizing of the panel
	private void resize(){
		int x = VideoFrame.getInstance().getWidth();
		int y = VideoFrame.getInstance().getHeight();

		//Math used to find the location of the J Components 
		play.setLocation((x/2)-34, y-65);
		pause.setLocation((x/2)-34, y-65);
		stop.setLocation((x/2)+2, y-65);
		back.setLocation((x/2)-71, y-65);
		forward.setLocation((x/2)+39, y-65);
		sound.setLocation(x-44, y-65);
		mute.setLocation(x-81,y-65);
		volume.setLocation(x-231, y-65);
		videoPanel.setSize(x, y-100);
		videoTimer.setLocation(x-65,y-100);
		videoSlider.setBounds(0, y-100, x-65, 25);
	}

	//This method allows the user to play a downloaded file
	public void playDownloadedVideo(String downloadedFile){
		VideoFrame.getInstance().original = new File(downloadedFile);
		video.stop();
		video.playMedia(VideoFrame.getInstance().original.getAbsolutePath());
		pause.setVisible(true);
		play.setVisible(false);
		play.setEnabled(true);
		toggleStopButtons(true);
		videoSliderClock.start();
	}

}