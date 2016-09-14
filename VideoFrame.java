package spelling;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class VideoFrame extends JFrame{
	private static VideoFrame frame;
	public File original;

	public static VideoFrame getInstance(){
		if (frame == null){
			frame = new VideoFrame();
		}
		return frame;
	}

	private VideoFrame() {
		// set up the frame for video
		this.setTitle("VIDEO REWARD");
		setSize(900, 400);
		this.setMinimumSize(new Dimension(900, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VideoFrame player = VideoFrame.getInstance();
					player.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
